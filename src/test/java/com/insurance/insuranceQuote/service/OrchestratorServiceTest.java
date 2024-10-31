package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.exception.InsurancePolicyException;
import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.exception.QuoteRequisitionException;
import com.insurance.insuranceQuote.model.dto.QuoteRequisition;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
public class OrchestratorServiceTest {


    @Mock
    private InsuranceQuotedService insuranceQuotedService;

    @Mock
    private CatalogService catalogService;

    @Mock
    private PolicyService policyService;

    @Spy
    @InjectMocks
    private OrchestratorService orchestratorService;

    QuotedInsurance quoted;
    private QuoteRequisition quote;

    @BeforeEach
    void setup(){
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
                .quotedId(1L)
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
                .createdAt(LocalDateTime.now())
                .totalMonthlyPremiumAmount(75.25)
                .build();
    }

    @Test
    void handleQuoteRequisitionShouldThrowQuoteRequisitionException() throws CatalogServiceRequestException  {
        when(catalogService.validateQuoteRequisition(quote)).thenThrow(CatalogServiceRequestException.class);

        QuoteRequisitionException ex =  assertThrows(QuoteRequisitionException.class, () -> {
            orchestratorService.handleQuoteRequisition(quote);
        });
        assertThat(ex.getMessage()).contains("Unable to validate offer and product for this quote requisition.");

    }

    @Test
    void handleQuoteRequisitionShouldThrowInsuranceQuotedExceptionAndRevert() throws CatalogServiceRequestException, InsuranceQuotedException, QuoteRequisitionException {
        when(catalogService.validateQuoteRequisition(quote)).thenReturn(quoted);
        doThrow(InsuranceQuotedException.class).when(insuranceQuotedService).persistInsuranceQuoted(any(QuotedInsurance.class));

        QuoteRequisitionException ex =  assertThrows(QuoteRequisitionException.class, () -> {
            orchestratorService.handleQuoteRequisition(quote);
        });

        verify(insuranceQuotedService, times(1)).revertInsuranceQuoted(any(Long.class));
        assertThat(ex.getMessage()).contains("An error occurred in persistence process. Error:");
    }

    @Test
    void sendMessagErrorShouldThrowInsuranceQuotedExceptionAndRevert() throws CatalogServiceRequestException, InsuranceQuotedException, QuoteRequisitionException, InsurancePolicyException {
        when(catalogService.validateQuoteRequisition(quote)).thenReturn(quoted);
        doNothing().when(insuranceQuotedService).persistInsuranceQuoted(any(QuotedInsurance.class));

        doThrow(InsurancePolicyException.class).when(policyService).sendInsurancePolicyMessage(any(QuotedInsurance.class));

        QuoteRequisitionException ex =  assertThrows(QuoteRequisitionException.class, () -> {
            orchestratorService.handleQuoteRequisition(quote);
        });

        verify(insuranceQuotedService, times(1)).revertInsuranceQuoted(any(Long.class));
        assertThat(ex.getMessage()).contains("Communication failed. Cant send quote to policy broker. Error:");
    }

    @Test
    void handleQuoteRequisitionShouldFinishWithSuccessAndLog(CapturedOutput output) throws CatalogServiceRequestException, InsuranceQuotedException, QuoteRequisitionException, InsurancePolicyException {

        when(catalogService.validateQuoteRequisition(quote)).thenReturn(quoted);
        doNothing().when(insuranceQuotedService).persistInsuranceQuoted(any(QuotedInsurance.class));
        doNothing().when(policyService).sendInsurancePolicyMessage(any(QuotedInsurance.class));

        orchestratorService.handleQuoteRequisition(quote);

        assertThat(output).contains("Persisted and sent to policy broker successfully");
    }








}
