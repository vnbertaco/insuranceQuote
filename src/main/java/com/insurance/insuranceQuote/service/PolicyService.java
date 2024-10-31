package com.insurance.insuranceQuote.service;


import com.insurance.insuranceQuote.exception.InsurancePolicyException;
import com.insurance.insuranceQuote.model.dto.InsurancePolicyResponse;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;

public interface PolicyService {
    void sendInsurancePolicyMessage(QuotedInsurance quotedInsurance) throws InsurancePolicyException;

    void receivedPolicy(InsurancePolicyResponse policyResponse);
}
