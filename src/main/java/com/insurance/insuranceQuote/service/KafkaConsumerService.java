package com.insurance.insuranceQuote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.model.dto.InsurancePolicyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class KafkaConsumerService {

    @Autowired
    private PolicyService policyService;

    /**
     * Método invocado pela chegada de uma mensagem kafka do broker de politicas de seguro
     * @param message
     */
    @KafkaListener(topics = "${consumer.topic.name}")
    public void receivedMessage(String message){
        try {
            log.info("Received policy update:{}", message);
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.registerModule(new JavaTimeModule());

            InsurancePolicyResponse policyResponse = objMapper.readValue(message, InsurancePolicyResponse.class);

            policyService.receivedPolicy(policyResponse);
        }catch (Exception e) {
            log.error("Error receiving policy message: {}", e.getMessage());
        }
    }
}
