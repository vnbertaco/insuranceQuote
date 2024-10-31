package com.insurance.insuranceQuote.service;

import com.insurance.insuranceQuote.exception.CatalogServiceRequestException;
import com.insurance.insuranceQuote.model.dto.InsuranceOfferResponse;
import com.insurance.insuranceQuote.model.dto.InsuranceProductResponse;
import com.insurance.insuranceQuote.model.dto.QuoteRequisition;
import com.insurance.insuranceQuote.model.entity.QuotedInsurance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.insurance.insuranceQuote.util.InsuranceMapper.insuranceRequisitionToQuoted;

@Service
@Slf4j
public class CatalogServiceImplementation implements CatalogService {

    @Autowired
    private RestClientServiceImplementation restClientService;

    @Override
    public QuotedInsurance validateQuoteRequisition(QuoteRequisition quoteRequisition) throws CatalogServiceRequestException{

            InsuranceProductResponse insuranceProduct = requestProductToCatalog(quoteRequisition.getProductId());
            InsuranceOfferResponse insuranceOffer = requestOfferToCatalog(quoteRequisition.getOfferId());

            validateProductandOffer(insuranceOffer, insuranceProduct, quoteRequisition);
            return insuranceRequisitionToQuoted(quoteRequisition);
    }

    /**
     * Executa validações de regras de negocio da solicitação de cotação de seguro
     * @param insuranceOffer
     * @param insuranceProduct
     * @param quoteRequisition
     * @throws CatalogServiceRequestException
     */
    private static void validateProductandOffer(InsuranceOfferResponse insuranceOffer, InsuranceProductResponse insuranceProduct, QuoteRequisition quoteRequisition) throws CatalogServiceRequestException {

            if (insuranceOffer == null || insuranceProduct == null) {
                throw new CatalogServiceRequestException("Invalid product or offer.");
            }

            if (insuranceOffer.isActive() == false || insuranceOffer.isActive() == false) {
                throw new CatalogServiceRequestException("Product or offer is not active.");
            }

            if(isCoveragesInvalid(insuranceOffer.getCoverages(), quoteRequisition.getCoverageAmounts())){
                throw new CatalogServiceRequestException("Invalid Coverages values in requisition.");
            }

            if(insuranceOffer.getAssistances().containsAll(quoteRequisition.getAssistances()) == false){
                throw new CatalogServiceRequestException("Invalid Assistance in quote requisition.");
            }

            if (!isTotalMonthlyPremiumAmountInRange(quoteRequisition.getTotalMonthlyPremiumAmount(), insuranceOffer)) {
                throw new CatalogServiceRequestException("Invalid Monthly Premium Amount.");
            }

            if (quoteRequisition.getCoverageAmounts().values().stream().collect(Collectors.summarizingDouble(Double::doubleValue)).getSum() !=
            quoteRequisition.getTotalCoverageAmount()){
                throw new CatalogServiceRequestException("Invalid Total Coverage Amount.");
            }

    }

    /**
     * Retorna verdadeiro se houver inconsistencia com uma das coberturas da cotacao
     * @param offer
     * @param requisition
     * @return
     */
    private static boolean isCoveragesInvalid(Map<java.lang.String, java.lang.Double> offer,
                                              Map<java.lang.String, java.lang.Double> requisition){
            //invalido
            if(requisition.entrySet().stream()
                    .anyMatch(c-> (offer.get(c.getKey()) < c.getValue())))
                return true;

            return false;
    }

    /**
     * Retorna verdadeiro se o valor do premio mensal estiver dentro do intervalo valido
     * @param totalMonthlyPremiumAmount
     * @param insuranceOffer
     * @return
     */
    private static boolean isTotalMonthlyPremiumAmountInRange(Double totalMonthlyPremiumAmount, InsuranceOfferResponse insuranceOffer) {

        //valido
        if(insuranceOffer.getMonthlyPremiumAmount().get("max_amount") > totalMonthlyPremiumAmount && (insuranceOffer.getMonthlyPremiumAmount().get("min_amount") < totalMonthlyPremiumAmount))
            return true;

        return false;
    }

    /**
     * Solicita dados de oferta ao servico responsavel pela comunicação REST com o Catalogo
     * @param offerId
     * @return
     * @throws CatalogServiceRequestException
     */
    private InsuranceOfferResponse requestOfferToCatalog(String offerId) throws CatalogServiceRequestException {
        Map<String, String> params = new HashMap<>();
        params.put("offer_id", offerId);

       return restClientService.requestOfferToCatalogRestClient(params);
    }

    /**
     * Solicita dados de produto ao servico responsavel pela comunicação REST com o Catalogo
     * @param productId
     * @return
     * @throws CatalogServiceRequestException
     */
    private InsuranceProductResponse requestProductToCatalog(String productId) throws CatalogServiceRequestException {
        Map<String, String> params = new HashMap<>();
        params.put("product_id", productId);

        return restClientService.requestProductToCatalog(params);
    }

}
