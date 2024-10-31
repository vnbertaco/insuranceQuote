package com.insurance.insuranceQuote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.model.dto.InsuranceOfferResponse;
import com.insurance.insuranceQuote.model.dto.InsuranceProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class RestClientServiceImplementation implements RestClientService {

    @Value("${catalog.base.url}")
    private String baseUrl;

    private RestClient restClient = RestClient.create();

    /**
     * Reponsavel pela solicitacao REST de ofertas ao servico de catalogo
     * @param params
     * @return
     * @throws CatalogServiceRequestException
     */
    @Cacheable("offers")
    @Override
    public InsuranceOfferResponse requestOfferToCatalogRestClient(Map<String, String> params) throws CatalogServiceRequestException {

        try {
            var response = restClient.post()
                    .uri(baseUrl + "/insurance/offer")
                    .header("Content-Type", "application/json")
                    .body(new ObjectMapper().writeValueAsString(params))
                    .retrieve()
                    .body(InsuranceOfferResponse.class);

            return response;
        }catch (Exception e ){
            throw new CatalogServiceRequestException(e.getMessage());
        }
    }

    /**
     * Reponsavel pela solicitacao REST de produtos ao servico de catalogo
     * @param params
     * @return
     * @throws CatalogServiceRequestException
     */
    @Cacheable("products")
    @Override
    public InsuranceProductResponse requestProductToCatalog(Map<String, String> params) throws CatalogServiceRequestException {

        try {
            var response = restClient.post()
                    .uri(baseUrl + "/insurance/product")
                    .header("Content-Type", "application/json")
                    .body(new ObjectMapper().writeValueAsString(params))
                    .retrieve()
                    .body(InsuranceProductResponse.class);

            return response;
        }catch (Exception e ){
            throw new CatalogServiceRequestException(e.getMessage());
        }
    }

}
