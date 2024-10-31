package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.exception.InsurancePolicyException;
import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.exception.QuoteRequisitionException;
import com.insurance.insuranceQuote.model.dto.QuoteRequisition;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shaded_package.javax.validation.Valid;

@Service
@Slf4j
public class OrchestratorService {

    private InsuranceQuotedService insuranceQuotedService;
    private CatalogService catalogService;
    private PolicyService policyService;

    @Autowired
    public OrchestratorService(InsuranceQuotedService insuranceQuotedService, CatalogService catalogService, PolicyService policyService){
        this.insuranceQuotedService = insuranceQuotedService;
        this.catalogService = catalogService;
        this.policyService = policyService;
    }

    /**
     * Orquestrador do processo de cotação de seguro disparado a partir de uma requisição REST
     * @param quoteRequisition
     * @return
     * @throws QuoteRequisitionException
     */
    public QuotedInsurance handleQuoteRequisition(@Valid QuoteRequisition quoteRequisition) throws QuoteRequisitionException {

        log.info("Insurance Requisition Received - ProductId: {}", quoteRequisition.getProductId());

        QuotedInsurance quotedInsurance = null;

        /* receber requisicao e consultar catalogo
            em caso de falha apena lancar excecao no controller
         */
        try{
            log.debug("Requesting Product and Offer to Catalog Service");
            quotedInsurance = catalogService.validateQuoteRequisition(quoteRequisition);

        }catch (CatalogServiceRequestException ce){
            log.error("Insurance Quoted Validation Error:{}", ce.getLocalizedMessage());
            throw new QuoteRequisitionException("Unable to validate offer and product for this quote requisition. "+ce.getMessage());
        }

        /* persistir cotacao e enviar mensagem na fila ao broker policies
            em falha de falha na persistencia reverter a cotacao
            reverter a cotacao apagando do banco e lancando excecao no controller
         */
        try{
            insuranceQuotedService.persistInsuranceQuoted(quotedInsurance);
            policyService.sendInsurancePolicyMessage(quotedInsurance);

            log.info("Insurance Quote ID: {} Persisted and sent to policy broker successfully.", quotedInsurance.getQuotedId());

        }catch (InsuranceQuotedException e){
            log.error("Reverted. Insurance Quote Persistence Error:{}",e.getLocalizedMessage());
            insuranceQuotedService.revertInsuranceQuoted(quotedInsurance.getQuotedId());
            throw new QuoteRequisitionException("An error occurred in persistence process. Error: "+e.getLocalizedMessage());

        }catch (InsurancePolicyException insurancePolicyException){
            log.error("Reverted. Can't send insurance's policy request message. Error:{}",insurancePolicyException.getLocalizedMessage());
            insuranceQuotedService.revertInsuranceQuoted(quotedInsurance.getQuotedId());
            throw new QuoteRequisitionException("Communication failed. Cant send quote to policy broker. Error: "+insurancePolicyException.getLocalizedMessage());

        }
        return quotedInsurance;
    }

}
