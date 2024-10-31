package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.data.InsuranceQuotedRepository;
import com.insurance.insuranceQuote.exception.InsuranceQuotedException;
import com.insurance.insuranceQuote.model.dto.InsurancePolicyResponse;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class InsuranceQuotedServiceImplementation implements InsuranceQuotedService {

    private InsuranceQuotedRepository quotedRepository;

    @Autowired
    public InsuranceQuotedServiceImplementation(InsuranceQuotedRepository quotedRepository){
        this.quotedRepository = quotedRepository;
    }

    /**
     * Persistencia da cotação de seguro validada
     * @param quotedInsurance
     * @throws InsuranceQuotedException
     */
    @Transactional
    @Override
    public void persistInsuranceQuoted(QuotedInsurance quotedInsurance) throws InsuranceQuotedException {
        try {
            quotedRepository.save(quotedInsurance);
        }catch (Exception e){
            throw new InsuranceQuotedException("An error ocurred when saving Insurance. QuotedId:"+ quotedInsurance.getQuotedId());
        }
    }

    /**
     * Reverte a persistencia da cotação em caso de falha
     * @param quotedId
     */
    @Override
    @Transactional
    public void revertInsuranceQuoted(Long quotedId) {
        Optional<QuotedInsurance> insurance = quotedRepository.findById(quotedId);
        if(insurance.isPresent()){
            quotedRepository.delete(insurance.get());
        }
    }

    /**
     * Atualiza a cotação de seguro com os dados recebidos do broker
     * @param policyResponse
     * @throws InsuranceQuotedException
     */
    @Override
    public void updateInsuranceQuoted(InsurancePolicyResponse policyResponse) throws InsuranceQuotedException{
      try {
          Optional<QuotedInsurance> insurance = quotedRepository.findById(policyResponse.getQuotedId());
          if (insurance.isEmpty()) {
              throw new InsuranceQuotedException("Quoted Insurance not found. QuotedId: " + policyResponse.getQuotedId());
          }

          insurance.get().setInsurancePolicyId(policyResponse.getInsurancePolicyId());
          insurance.get().setUpdateAt(LocalDateTime.now());

          quotedRepository.save(insurance.get());

          log.info("Updated quote ID:{} with received policyId: {} ",insurance.get().getQuotedId(), insurance.get().getInsurancePolicyId());
      }catch (Exception e){
          throw new InsuranceQuotedException("Error updating insurance. "+e.getMessage());
      }

    }

    /**
     * Recupera uma cotação armazenada a partir do ID
     * @param quotedId
     * @return
     * @throws InsuranceQuotedException
     */
    @Override
    public QuotedInsurance getQuotedInsurance(String quotedId) throws InsuranceQuotedException {

        Optional<QuotedInsurance> quoted = quotedRepository.findById(Long.parseLong(quotedId));
        if(quoted.isEmpty()){
            throw new InsuranceQuotedException("Quoted Insurance not found");
        }

        return quoted.get();
    }


}
