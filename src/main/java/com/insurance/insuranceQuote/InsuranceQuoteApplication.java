package com.insurance.insuranceQuote;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class InsuranceQuoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceQuoteApplication.class, args);
	}

}
