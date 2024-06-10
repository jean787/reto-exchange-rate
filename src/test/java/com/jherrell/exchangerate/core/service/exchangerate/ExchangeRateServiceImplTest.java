package com.jherrell.exchangerate.core.service.exchangerate;

import com.jherrell.exchangerate.core.Exception.ApiException;
import com.jherrell.exchangerate.core.model.ExchangeRateInquiryResponse;
import com.jherrell.exchangerate.core.model.ExchangeRateRequest;
import com.jherrell.exchangerate.core.model.ExchangeRateResponse;
import com.jherrell.exchangerate.core.model.PairConversionResponse;
import com.jherrell.exchangerate.infrastructure.config.ApplicationProperties;
import com.jherrell.exchangerate.infrastructure.entity.ExchangeRateRegisterEntity;
import com.jherrell.exchangerate.infrastructure.repository.ExchangeRateRegisterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceImplTest {

    @InjectMocks
    private ExchangeRateServiceImpl service;
    @Mock
    private ExchangeRateSender exchangeRateSender;
    @Mock
    private ExchangeRateRegisterRepository exchangeRateRepository;
    @Mock
    private ApplicationProperties properties;

    private String token
            = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUMTQ5NjkiLCJpYXQiOjE3MTgwMDk4OTUsImV4cCI6NjQwODc3MzY0MDB9.5z8MQdLM5IsbcILrmNWmOBM4wN_50uIKjnop1onOY8k";

    @Test
    void executeExchangeRate_success() {
        // Arrange
        ExchangeRateRequest request = new ExchangeRateRequest(BigDecimal.valueOf(100), "PEN", "USD");
        HashMap<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", BigDecimal.valueOf(0.38));
        PairConversionResponse conversionResponse = PairConversionResponse.builder()
                .rates(rates)
                .build();
        ExchangeRateRegisterEntity exchangeRateEntity = ExchangeRateRegisterEntity.builder()
                .targetRate(BigDecimal.valueOf(0.38))
                .baseAmount(BigDecimal.valueOf(100))
                .build();

        when(exchangeRateSender.sendAndReceive(request)).thenReturn(Mono.just(conversionResponse));
        when(properties.getSecret()).thenReturn("586S3272357838782F413F7428472B4U4550655568666B597033733676397524");
        when(exchangeRateRepository.save(any())).thenReturn(exchangeRateEntity);

        // Act
        Mono<ExchangeRateResponse> result = service.executeExchangeRate(request, token);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTargetRate().compareTo(BigDecimal.valueOf(0.38)) == 0)
                .verifyComplete();
    }

    @Test
    void executeExchangeRate_error() {
        // Arrange
        ExchangeRateRequest request = new ExchangeRateRequest(BigDecimal.valueOf(100), "PEN", "USD");
        RuntimeException exception = new RuntimeException("Error occurred");

        when(exchangeRateSender.sendAndReceive(request)).thenReturn(Mono.error(exception));

        // Act
        Mono<ExchangeRateResponse> result = service.executeExchangeRate(request, token);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ApiException && throwable.getMessage().equals("Error occurred"))
                .verify();
    }

    @Test
    void inquiryExchangeRate_success() {
        // Arrange
        ExchangeRateRegisterEntity exchangeRateEntity = ExchangeRateRegisterEntity.builder()
                .id(1L)
                .targetRate(BigDecimal.valueOf(0.38))
                .baseAmount(BigDecimal.valueOf(100))
                .dateTime(new Date())
                .build();
        List<ExchangeRateRegisterEntity> exchangeRateEntities = Arrays.asList(exchangeRateEntity);

        when(exchangeRateRepository.findAll()).thenReturn(exchangeRateEntities);

        // Act
        Flux<ExchangeRateInquiryResponse> result = service.inquiryExchangeRate();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

}
