package com.jherrell.exchangerate.core.service.exchangerate;

import static com.jherrell.exchangerate.core.mapper.DataMapper.mapExchangeRateEntityToResponse;
import static com.jherrell.exchangerate.core.mapper.DataMapper.mapResponseToExchangeRateEntity;

import com.jherrell.exchangerate.core.Exception.ApiException;
import com.jherrell.exchangerate.core.mapper.DataMapper;
import com.jherrell.exchangerate.core.model.ExchangeRateInquiryResponse;
import com.jherrell.exchangerate.core.model.ExchangeRateRequest;
import com.jherrell.exchangerate.core.model.ExchangeRateResponse;
import com.jherrell.exchangerate.core.model.PairConversionResponse;
import com.jherrell.exchangerate.infrastructure.config.ApplicationProperties;
import com.jherrell.exchangerate.infrastructure.entity.ExchangeRateRegisterEntity;
import com.jherrell.exchangerate.infrastructure.jwt.JwtService;
import com.jherrell.exchangerate.infrastructure.repository.ExchangeRateRegisterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private ExchangeRateSender exchangeRateSender;
    private ExchangeRateRegisterRepository exchangeRateRepository;
    private ApplicationProperties properties;

    @Override
    public Mono<ExchangeRateResponse> executeExchangeRate(
            ExchangeRateRequest request, String token) {

        return exchangeRateSender.sendAndReceive(request)
                .flatMap(pairConversionResponse -> Mono.just(processExchangeRate(request, pairConversionResponse, token)))
                .flatMap(exchangeRateRegisterEntity -> Mono.just(exchangeRateRepository.save(exchangeRateRegisterEntity)))
                .flatMap(exchangeRateRegisterEntity -> Mono.just(mapExchangeRateEntityToResponse(exchangeRateRegisterEntity)))
                .doOnError(ex -> log.error("[ExchangeRateServiceImpl - executeExchangeRate]"
                        + "Error occurred at processing exchange rate service",ex))
                .onErrorResume(ex ->
                        Mono.error(new ApiException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @Override
    public Flux<ExchangeRateInquiryResponse> inquiryExchangeRate() {

        return Flux.fromIterable(exchangeRateRepository.findAll())
                .map(DataMapper::mapExchangeRateEntityToInquiryResponse)
                .doOnError(ex -> log.error("[ExchangeRateServiceImpl - inquiryExchangeRate]"
                        + "Error occurred in the inquiry service",ex))
                .onErrorResume(ex ->
                        Mono.error(new ApiException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));

    }

    private ExchangeRateRegisterEntity processExchangeRate(
            ExchangeRateRequest request, PairConversionResponse conversionResponse, String token) {
        BigDecimal targetRate = conversionResponse.getRates().get(request.getTargetCurrency());
        BigDecimal conversionAmount = request.getAmount().multiply(targetRate);
        String username = JwtService.getUsernameFromToken(
                token.replace("Bearer ", ""), properties.getSecret());

        return mapResponseToExchangeRateEntity(request, conversionAmount, targetRate, username);
    }

}
