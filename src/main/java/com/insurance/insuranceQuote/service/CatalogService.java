package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.model.dto.QuoteRequisition;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;

public interface CatalogService {
    public QuotedInsurance validateQuoteRequisition(QuoteRequisition quoteRequisition) throws CatalogServiceRequestException ;

}
