package com.insurance.insuranceQuote.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.insurance.insuranceQuote.exception.InsurancePolicyException;
import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.model.dto.InsurancePolicyResponse;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PolicyServiceImplementation implements PolicyService{

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private InsuranceQuotedService quotedService;

    /**
     * Formata uma mensagem a ser enviada ao serviço de politicas e requisita o envio ao serviço kafka responsável
     * @param quotedInsurance
     * @throws InsurancePolicyException
     */
    @Override
    public void sendInsurancePolicyMessage(QuotedInsurance quotedInsurance) throws InsurancePolicyException  {
       try {
           ObjectMapper objectMapper = new ObjectMapper();
           objectMapper.registerModule(new JavaTimeModule());
           kafkaProducerService.sendMessage(objectMapper.writeValueAsString(quotedInsurance));
        }catch (JsonProcessingException jsonProcessingException){
            throw new InsurancePolicyException("Unable to send message to policy broker. Error: "+ jsonProcessingException.getLocalizedMessage());
        }
    }

    /**
     * Solicita ao serviço de cotações que atualize uma cotação com de acordo com a mensagem recebida do servico de politicas
     * @param policyResponse
     */
    @Override
    public void receivedPolicy(InsurancePolicyResponse policyResponse){
        try {
            quotedService.updateInsuranceQuoted(policyResponse);
        }catch (  InsuranceQuotedException e) {
            log.error("Error updating policy message: {}", e.getMessage());
        }
    }
}
