package com.insurance.insuranceQuote.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class QuoteRequisition {

    @NotBlank
    @JsonProperty("product_id")
    private String productId;

    @NotBlank
    @JsonProperty("offer_id")
    private String offerId;

    @NotBlank
    @JsonProperty("category")
    private String category;

    @JsonProperty("total_monthly_premium_amount")
    @Positive
    private Double totalMonthlyPremiumAmount;

    @JsonProperty("total_coverage_amount")
    @Positive
    private Double totalCoverageAmount;

    @NotEmpty
    @JsonProperty("coverages")
    private Map<String, Double> coverageAmounts;

    @NotEmpty
    @JsonProperty("assistances")
    private List<String> assistances;

    @NotEmpty
    @JsonProperty("customer")
    private Map<String ,String> customerData;


}
