package com.insurance.insuranceQuote.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QuoteRequisitionException extends Exception {

    public  QuoteRequisitionException(String message){
        super(message);
    }

    public Map<String, String> getFormatedMessage(){
        Map<String, String> output =   new HashMap<String, String>() {{
            put("message",  getLocalizedMessage());
            put("detail", Arrays.toString(getStackTrace()));
        }};
        return output;
    }

}
