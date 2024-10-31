package com.insurance.insuranceQuote.util;

import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.model.dto.QuoteRequisition;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;

import java.time.LocalDateTime;

/**
 * Cria um objeto de cotação a partir da requisicao validada
 */
public class InsuranceMapper {

    private InsuranceMapper(){}

    public static QuotedInsurance insuranceRequisitionToQuoted(QuoteRequisition quoteRequisition) throws CatalogServiceRequestException {
        try {
            return QuotedInsurance.builder()
                    .category(quoteRequisition.getCategory())
                    .assistances(quoteRequisition.getAssistances())
                    .customer(quoteRequisition.getCustomerData())
                    .coverages(quoteRequisition.getCoverageAmounts())
                    .totalMonthlyPremiumAmount(quoteRequisition.getTotalMonthlyPremiumAmount())
                    .offerId(quoteRequisition.getOfferId())
                    .createdAt(LocalDateTime.now())
                    .productId(quoteRequisition.getProductId())
                    .build();
        }catch (Exception e){
            throw new CatalogServiceRequestException(e.getMessage());
        }

    }
}
