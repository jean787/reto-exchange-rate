package com.jherrell.exchangerate.infrastructure.proxy;

import com.jherrell.exchangerate.core.model.PairConversionResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ExchangeRateClient {

    private WebClient exchangeRateClient;

    public Mono<PairConversionResponse> getExchangeRate(String baseCurrency) {
        return exchangeRateClient.get()
                .uri("v6/latest/{baseCurrency}", baseCurrency)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PairConversionResponse.class);
    }
}
