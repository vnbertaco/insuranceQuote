package com.insurance.insuranceQuote.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import shaded_package.javax.validation.constraints.Positive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotedInsurance implements Serializable {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long quotedId;

    @JsonProperty("insurance_policy_id")
    private Long insurancePolicyId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("offer_id")
    private String offerId;

    @JsonProperty("category")
    private String category;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("update_at")
    private LocalDateTime updateAt;

    @JsonProperty("total_monthly_premium_amount")
    @Positive
    private Double totalMonthlyPremiumAmount;

    @JsonProperty("assistances")
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> assistances;

    @JsonProperty("customer")
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String ,String> customer = new HashMap<>();

    @JsonProperty("coverages")
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Double> coverages = new HashMap<>();

}
