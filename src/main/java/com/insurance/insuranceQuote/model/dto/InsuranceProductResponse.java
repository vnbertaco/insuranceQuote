package com.insurance.insuranceQuote.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InsuranceProductResponse {

    @JsonProperty("id")
    private String productId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("offers")
    private List<String> offers;
}
