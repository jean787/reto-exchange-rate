package com.jherrell.exchangerate.core.service.exchangerate;

import com.jherrell.exchangerate.core.model.ExchangeRateInquiryResponse;
import com.jherrell.exchangerate.core.model.ExchangeRateRequest;
import com.jherrell.exchangerate.core.model.ExchangeRateResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeRateService {

    Mono<ExchangeRateResponse> executeExchangeRate(
            ExchangeRateRequest request, String authorizationHeader);

    Flux<ExchangeRateInquiryResponse> inquiryExchangeRate();
}
