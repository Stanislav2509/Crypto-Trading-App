package com.cryptotrading.Crypto.Trading.App.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestAPIConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}