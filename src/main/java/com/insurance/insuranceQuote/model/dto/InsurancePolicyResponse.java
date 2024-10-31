package com.insurance.insuranceQuote.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsurancePolicyResponse {

    @JsonProperty("id")
    private Long quotedId;

    @JsonProperty("insurance_policy_id")
    private Long insurancePolicyId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

}
