package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.exception.QuoteRequisitionException;
import com.insurance.insuranceQuote.model.dto.InsurancePolicyResponse;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;

public interface InsuranceQuotedService {

    void persistInsuranceQuoted(QuotedInsurance quotedInsurance) throws QuoteRequisitionException, InsuranceQuotedException;

    void revertInsuranceQuoted(Long quotedId) throws QuoteRequisitionException;

    void updateInsuranceQuoted(InsurancePolicyResponse insurance) throws InsuranceQuotedException;

    QuotedInsurance getQuotedInsurance(String quotedId) throws InsuranceQuotedException;
}
