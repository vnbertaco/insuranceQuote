package com.insurance.insuranceQuote.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class InsuranceOfferResponse {

    @JsonProperty("id")
    private String offerId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("coverages")
    private Map<String, Double> coverages;

    @JsonProperty("assistances")
    private List<String> assistances;

    @JsonProperty("monthly_premium_amount")
    private Map<String,Double> monthlyPremiumAmount;

}
