package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.data.InsuranceQuotedRepository;
import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.exception.QuoteRequisitionException;
import com.insurance.insuranceQuote.model.dto.InsurancePolicyResponse;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
public final class QuotedInsuranceServiceTest {

    @Mock
    private InsuranceQuotedRepository quotedRepository;
    @Spy
    @InjectMocks
    private InsuranceQuotedService quotedService =  new InsuranceQuotedServiceImplementation(quotedRepository);

    @Test
    void persistShouldThrowInsuranceQuotedException() throws InsuranceQuotedException, QuoteRequisitionException {

        when(quotedRepository.save(ArgumentMatchers.any(QuotedInsurance.class))).thenThrow(RuntimeException.class);
        InsuranceQuotedException ex =  assertThrows(InsuranceQuotedException.class, () -> {
            quotedService.persistInsuranceQuoted(new QuotedInsurance());
        });
        assertThat(ex.getMessage()).contains("An error ocurred when saving Insurance.");
    }

    @Test
    void updateInsuranceShouldThrowInsuranceQuotedException() throws InsuranceQuotedException, QuoteRequisitionException {

            InsurancePolicyResponse pol = new InsurancePolicyResponse();
            pol.setQuotedId(0L);

        doReturn(Optional.empty()).when(quotedRepository).findById(any(Long.class));
        InsuranceQuotedException ex =  assertThrows(InsuranceQuotedException.class, () -> {
            quotedService.updateInsuranceQuoted(pol);
        });
        assertThat(ex.getMessage()).contains("Quoted Insurance not found.");

        QuotedInsurance quoted = QuotedInsurance.builder().quotedId(0L).build();
        doReturn(Optional.of(quoted)).when(quotedRepository).findById(any(Long.class));
        when(quotedRepository.save(ArgumentMatchers.any(QuotedInsurance.class))).thenThrow(RuntimeException.class);
         ex =  assertThrows(InsuranceQuotedException.class, () -> {
             quotedService.updateInsuranceQuoted(pol);
        });
        assertThat(ex.getMessage()).contains("Error updating insurance.");
    }

    @Test
    void updateInsuranceShouldFinishSucess(CapturedOutput output) throws InsuranceQuotedException, QuoteRequisitionException {

        InsurancePolicyResponse pol = new InsurancePolicyResponse();
        pol.setQuotedId(0L);
        pol.setInsurancePolicyId(0L);

        QuotedInsurance quoted = QuotedInsurance.builder().quotedId(0L).build();

        when(quotedRepository.save(any(QuotedInsurance.class))).thenReturn(quoted);
        doReturn(Optional.of(quoted)).when(quotedRepository).findById(any(Long.class));
        quotedService.updateInsuranceQuoted(pol);

        String expected = String.format("Updated quote ID:%d with received policyId: %d ", quoted.getQuotedId(), pol.getQuotedId());
        assertThat(output).contains(expected);
    }

    @Test
    void getQuotedInsuranceShouldFinishSuccess(CapturedOutput output) throws InsuranceQuotedException, QuoteRequisitionException {

        InsurancePolicyResponse pol = new InsurancePolicyResponse();
        pol.setQuotedId(0L);
        pol.setInsurancePolicyId(0L);

        QuotedInsurance quoted = QuotedInsurance.builder().quotedId(0L).build();

        doReturn(Optional.of(quoted)).when(quotedRepository).findById(any(Long.class));
        QuotedInsurance result = quotedService.getQuotedInsurance(String.valueOf(pol.getQuotedId()));

        Assertions.assertEquals(result,quoted);

    }

    @Test
    void getQuotedInsuranceShouldThrow() throws InsuranceQuotedException, QuoteRequisitionException {

        InsurancePolicyResponse pol = new InsurancePolicyResponse();
        pol.setQuotedId(0L);
        pol.setInsurancePolicyId(0L);

        QuotedInsurance quoted = QuotedInsurance.builder().quotedId(0L).build();

        doReturn(Optional.empty()).when(quotedRepository).findById(any(Long.class));

        InsuranceQuotedException ex =  assertThrows(InsuranceQuotedException.class, () -> {
            quotedService.getQuotedInsurance(String.valueOf(pol.getQuotedId()));
        });
        assertThat(ex.getMessage()).contains(("Quoted Insurance not found"));

    }

    @Test
    void revertInsuranceQuotedShouldFinishSuccess() throws InsuranceQuotedException, QuoteRequisitionException {

        InsurancePolicyResponse pol = new InsurancePolicyResponse();
        pol.setQuotedId(0L);
        pol.setInsurancePolicyId(0L);

        QuotedInsurance quoted = QuotedInsurance.builder().quotedId(0L).build();

        doReturn(Optional.of(quoted)).when(quotedRepository).findById(any(Long.class));
        doNothing().when(quotedRepository).delete(any(QuotedInsurance.class));

        quotedService.revertInsuranceQuoted(quoted.getQuotedId());
        verify(quotedRepository, times(1)).delete(any(QuotedInsurance.class));
    }

}
