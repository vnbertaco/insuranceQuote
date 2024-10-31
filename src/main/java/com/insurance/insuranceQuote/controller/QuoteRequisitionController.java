package com.insurance.insuranceQuote.controller;

import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.exception.QuoteRequisitionException;
import com.insurance.insuranceQuote.model.dto.QuoteRequisition;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import com.insurance.insuranceQuote.service.InsuranceQuotedService;
import com.insurance.insuranceQuote.service.OrchestratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shaded_package.javax.validation.Valid;

@Slf4j
@RestController
public class QuoteRequisitionController
{
    @Autowired
    private OrchestratorService orchestratorService;

    @Autowired
    private InsuranceQuotedService insuranceQuotedService;

    @PostMapping(value="/insurance/quote")
    public ResponseEntity quoteRequisition(@RequestBody @Valid QuoteRequisition quoteRequisition){
        try{
            QuotedInsurance quoted = orchestratorService.handleQuoteRequisition(quoteRequisition);
            return ResponseEntity.status(HttpStatus.OK).body(quoted);
        }catch (QuoteRequisitionException requisitionException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(requisitionException.getFormatedMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error ocurred. Error: "+e.getLocalizedMessage());
        }
    }

    @GetMapping(value="/insurance/quote/{quotedId}")
    public ResponseEntity getQuotedInsurance(@PathVariable String quotedId){
        try {
            QuotedInsurance quoted = insuranceQuotedService.getQuotedInsurance(quotedId);
            return ResponseEntity.status(HttpStatus.OK).body(quoted);
        }catch (InsuranceQuotedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getLocalizedMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }
}
