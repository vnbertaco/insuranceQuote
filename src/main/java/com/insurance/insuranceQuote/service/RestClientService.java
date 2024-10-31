package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.model.dto.InsuranceOfferResponse;
import com.insurance.insuranceQuote.model.dto.InsuranceProductResponse;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;

public interface RestClientService {
    @Cacheable("offers")
    InsuranceOfferResponse requestOfferToCatalogRestClient(Map<String, String> params) throws CatalogServiceRequestException;

    @Cacheable("products")
    InsuranceProductResponse requestProductToCatalog(Map<String, String> params) throws CatalogServiceRequestException;
}
