package com.jherrell.exchangerate.core.service.exchangerate;

import com.jherrell.exchangerate.core.Exception.ApiException;
import com.jherrell.exchangerate.core.model.ExchangeRateRequest;
import com.jherrell.exchangerate.core.model.PairConversionResponse;
import com.jherrell.exchangerate.infrastructure.proxy.ExchangeRateClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@AllArgsConstructor
public class ExchangeRateSender {

    private ExchangeRateClient client;

    public Mono<PairConversionResponse> sendAndReceive(ExchangeRateRequest request) {

        return client.getExchangeRate(request.getSourceCurrency())
                .doOnError(ex -> log.error("[ExchangeRateSender - sendAndReceive]"
                        + " Error at calling api",ex))
                .onErrorResume(ex ->
                        Mono.error(new ApiException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
