package com.jherrell.exchangerate.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${rest.endpoints.exchangeRate.url}")
    private String exchangeRateBaseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.create(exchangeRateBaseUrl);
    }
}
