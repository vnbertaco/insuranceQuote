package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.model.dto.InsuranceOfferResponse;
import com.insurance.insuranceQuote.model.dto.InsuranceProductResponse;
import com.insurance.insuranceQuote.model.dto.QuoteRequisition;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(MockitoExtension.class)
public class CatalogServiceTest {

    @Mock
    RestClientServiceImplementation restClientService = new RestClientServiceImplementation();

    @Spy
    @InjectMocks
    private CatalogService catalogService = new CatalogServiceImplementation();

    private QuoteRequisition quote;
    private InsuranceOfferResponse offer;
    private InsuranceProductResponse product;
    private QuotedInsurance quoted;

    @BeforeEach
    void createRequisition(){
        quote = QuoteRequisition.builder()
                .assistances(Arrays.asList("Encanador", "Eletricista"))
                .offerId("adc56d77-348c-4bf0-908f-22d402ee715c")
                .productId("adc56d77-348c-4bf0-908f-22d402ee715c")
                .category("Home")
                .coverageAmounts(new HashMap<String, Double>() {{
                    put("Incêndio", 250000.00);
                    put("Desastres naturais", 500000.00);
                    put("Responsabiliadade civil", 75000.00);
                }})
                .customerData(new HashMap<String, String>() {{
                    put("name",  "John Wick");
                    put("gender", "MALE");
                }})
                .totalCoverageAmount(825000.00)
                .totalMonthlyPremiumAmount(75.25)
                .build();
        quoted = QuotedInsurance.builder()
                .assistances(Arrays.asList("Encanador", "Eletricista"))
                .offerId("adc56d77-348c-4bf0-908f-22d402ee715c")
                .productId("adc56d77-348c-4bf0-908f-22d402ee715c")
                .category("Home")
                .coverages(new HashMap<String, Double>() {{
                    put("Incêndio", 250000.00);
                    put("Desastres naturais", 500000.00);
                    put("Responsabiliadade civil", 75000.00);
                }})
                .customer(new HashMap<String, String>() {{
                    put("name",  "John Wick");
                    put("gender", "MALE");
                }})
                .build();
        offer = InsuranceOfferResponse.builder()
                .offerId("adc56d77-348c-4bf0-908f-22d402ee715c")
                .active(true)
                .assistances(
                        Arrays.asList("Encanador", "Eletricista", "Chaveiro 24h","Assistência Funerária")
                )
                .monthlyPremiumAmount(
                        new HashMap<String, Double>() {{
                            put("max_amount", 100.74);
                            put("min_amount",  50.00);
                            put("suggested_amount", 60.25);
                        }}
                )
                .coverages(new HashMap<String, Double>() {{
                    put("Incêndio",  500000.00);
                    put("Desastres naturais",  600000.00);
                    put("Responsabiliadade civil",  80000.00);
                    put("Roubo", 100000.00);
                }})
                .build();

        product = InsuranceProductResponse.builder()
                .productId("1b2da7cc-b367-4196-8a78-9cfeec21f587")
                .active(true)
                .offers( Arrays.asList("adc56d77-348c-4bf0-908f-22d402ee715c"))
                .build();
    }


    @Test
    void validateQuoteRequisitionShouldThrowException( ){


        assertThrows(CatalogServiceRequestException.class, () -> {
            catalogService.validateQuoteRequisition(quote);
        });
    }



    @Test
    void inactiveShouldThrowCatalogException( ) throws CatalogServiceRequestException {
        product.setActive(false);
        offer.setActive(false);

        when(restClientService.requestProductToCatalog(any(Map.class))).thenReturn(product);
        when(restClientService.requestOfferToCatalogRestClient(any(Map.class))).thenReturn(offer);

        CatalogServiceRequestException exception = assertThrows(CatalogServiceRequestException.class, () -> {
            catalogService.validateQuoteRequisition(quote);
        });
        assertThat(exception.getMessage()).contains("Product or offer is not active.");

        product.setActive(true);
        when(restClientService.requestProductToCatalog(any(Map.class))).thenReturn(product);
        exception = assertThrows(CatalogServiceRequestException.class, () -> {
            catalogService.validateQuoteRequisition(quote);
        });
        assertThat(exception.getMessage()).contains("Product or offer is not active.");

    }

    @Test
    void invalidCoverageShouldThrowCatalogException( ) throws CatalogServiceRequestException {

        offer.setCoverages(
                new HashMap<String, Double>() {{
                    put("Incêndio", 500000.00);
                    put("Desastres naturais", 600000.00);
                }}
        );

        when(restClientService.requestProductToCatalog(any(Map.class))).thenReturn(product);
        when(restClientService.requestOfferToCatalogRestClient(any(Map.class))).thenReturn(offer);

        quote.setCoverageAmounts(
                new HashMap<String, Double>() {{
                    put("Incêndio", 600000.00);
                    put("Desastres naturais", 700000.00);
                }}
        );

        CatalogServiceRequestException exception = assertThrows(CatalogServiceRequestException.class, () -> {
            catalogService.validateQuoteRequisition(quote);
        });
        assertThat(exception.getMessage()).contains("Invalid Coverages values in requisition.");

    }

    @Test
    void invalidAssistanceShouldThrowCatalogException( ) throws CatalogServiceRequestException {

        offer.setAssistances(
                Arrays.asList("Encanador")
        );

        when(restClientService.requestProductToCatalog(any(Map.class))).thenReturn(product);
        when(restClientService.requestOfferToCatalogRestClient(any(Map.class))).thenReturn(offer);

        CatalogServiceRequestException exception = assertThrows(CatalogServiceRequestException.class, () -> {
            catalogService.validateQuoteRequisition(quote);
        });
        assertThat(exception.getMessage()).contains("Invalid Assistance in quote requisition.");

    }

    @Test
    void monthlyPremiumAmountOutofRangeShouldThrowCatalogException( ) throws CatalogServiceRequestException {
        quote.setTotalMonthlyPremiumAmount(25.0);

        when(restClientService.requestProductToCatalog(any(Map.class))).thenReturn(product);
        when(restClientService.requestOfferToCatalogRestClient(any(Map.class))).thenReturn(offer);

        CatalogServiceRequestException exception = assertThrows(CatalogServiceRequestException.class, () -> {
            catalogService.validateQuoteRequisition(quote);
        });
        assertThat(exception.getMessage()).contains("Invalid Monthly Premium Amount.");

    }

    @Test
    void invalidTotalCoverageShouldThrowCatalogException() throws CatalogServiceRequestException {
        quote.setTotalCoverageAmount(12356.89);

        when(restClientService.requestProductToCatalog(any(Map.class))).thenReturn(product);
        when(restClientService.requestOfferToCatalogRestClient(any(Map.class))).thenReturn(offer);

        CatalogServiceRequestException exception = assertThrows(CatalogServiceRequestException.class, () -> {
            catalogService.validateQuoteRequisition(quote);
        });
        assertThat(exception.getMessage()).contains("Invalid Total Coverage Amount.");

    }

    @Test
    void shouldFinishValidaWithSucess() throws CatalogServiceRequestException {

        when(restClientService.requestProductToCatalog(any(Map.class))).thenReturn(product);
        when(restClientService.requestOfferToCatalogRestClient(any(Map.class))).thenReturn(offer);

        QuotedInsurance result =  catalogService.validateQuoteRequisition(quote);

        Assertions.assertEquals(result.getCustomer(), quote.getCustomerData());

    }

}
