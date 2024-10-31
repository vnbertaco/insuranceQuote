package com.insurance.insuranceQuote.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.After;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Mock de um serviço rest para simular o serviço de catalogo
 */
@Service
public class MockServerTestService {

    private ClientAndServer mockServer;

    @Value("${catalog.base.url}")
    private String baseUrl;

    public MockServerTestService(){
        mockServer = startClientAndServer(1080);

        createValidResponseProduct();
        createValidResponseOffer();
    }



    @After("execution(* com.insurance.insuranceQuote.service.*.*(..))")
    public void stopMockServer(){
        if(mockServer != null){
            mockServer.stop();
        }
    }

    @Bean
    private void createValidResponseOffer(){
        Map<String, String> params = new HashMap<>();
        params.put("offer_id", "adc56d77-348c-4bf0-908f-22d402ee715c");
        try{
            mockServer
                    .when(
                            request()
                                    .withMethod("POST")
                                    .withPath("/insurance/offer")
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(new ObjectMapper().writeValueAsString(params))
                    ).respond(
                        response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{ "+
                                        "    \"id\": \"adc56d77-348c-4bf0-908f-22d402ee715c\"," +
                                        "    \"product_id\": \"1b2da7cc-b367-4196-8a78-9cfeec21f587\"," +
                                        "    \"name\": \"Seguro de Vida Familiar\"," +
                                        "    \"created_at\": \"2021-07-01T00:00:00Z\"," +
                                        "    \"active\": true," +
                                        "    \"coverages\": { "+
                                        "        \"Incêndio\": 500000.00," +
                                        "        \"Desastres naturais\": 600000.00," +
                                        "        \"Responsabiliadade civil\": 80000.00," +
                                        "        \"Roubo\": 100000.00 "+
                                        "    }," +
                                        "    \"assistances\": [" +
                                        "        \"Encanador\"," +
                                        "        \"Eletricista\"," +
                                        "        \"Chaveiro 24h\"," +
                                        "        \"Assistência Funerária\"" +
                                        "    ]," +
                                        "    \"monthly_premium_amount\": { "+
                                        "        \"max_amount\": 100.74," +
                                        "        \"min_amount\": 50.00," +
                                        "        \"suggested_amount\": 60.25 "+
                                        "    }" +
                                        "}")
                                .withDelay(TimeUnit.SECONDS,1)
                    );

        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    private void createValidResponseProduct() {
        Map<String, String> params = new HashMap<>();
        params.put("product_id", "1b2da7cc-b367-4196-8a78-9cfeec21f587");
        try{
            mockServer
                    .when(
                            request()
                                    .withMethod("POST")
                                    .withPath("/insurance/product")
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(new ObjectMapper().writeValueAsString(params))
                                     )
                    .respond(
                            response()
                                    .withStatusCode(200)
                                    .withHeader(
                                            new Header("Content-type", "application/json")
                                    )
                                    .withBody("{" +
                                            "    \"id\": \"1b2da7cc-b367-4196-8a78-9cfeec21f587\"," +
                                            "    \"name\": \"Seguro de Vida\"," +
                                            "    \"created_at\": \"2021-07-01T00:00:00Z\"," +
                                            "    \"active\": true," +
                                            "    \"offers\": [" +
                                            "        \"adc56d77-348c-4bf0-908f-22d402ee715c\"," +
                                            "        \"bdc56d77-348c-4bf0-908f-22d402ee715c\"," +
                                            "        \"cdc56d77-348c-4bf0-908f-22d402ee715c\"" +
                                            "    ]" +
                                            "}")
                                    .withDelay(TimeUnit.SECONDS,1)
                    );

        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }

    }

}
