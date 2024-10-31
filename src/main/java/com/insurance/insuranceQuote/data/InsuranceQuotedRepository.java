package com.insurance.insuranceQuote.data;

import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceQuotedRepository extends JpaRepository<QuotedInsurance, Long> {
}
