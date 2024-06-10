package com.jherrell.exchangerate.core.mapper;

import com.jherrell.exchangerate.core.model.ExchangeRateInquiryResponse;
import com.jherrell.exchangerate.core.model.ExchangeRateRequest;
import com.jherrell.exchangerate.core.model.ExchangeRateResponse;
import com.jherrell.exchangerate.core.model.TargetRateInquiry;
import com.jherrell.exchangerate.infrastructure.entity.ExchangeRateRegisterEntity;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

import static com.jherrell.exchangerate.core.common.Utils.generateDateWithFormat;
import static org.apache.commons.lang3.StringUtils.leftPad;

public class DataMapper {

    public static ExchangeRateRegisterEntity mapResponseToExchangeRateEntity(ExchangeRateRequest exchangeRateRequest,
            BigDecimal conversionAmount, BigDecimal targetRate, String username) {
        return ExchangeRateRegisterEntity.builder()
                .dateTime(new Date())
                .user(username)
                .baseAmount(exchangeRateRequest.getAmount())
                .conversionAmount(conversionAmount)
                .baseCurrency(exchangeRateRequest.getSourceCurrency())
                .targetCurrency(exchangeRateRequest.getTargetCurrency())
                .targetRate(targetRate)
                .build();
    }

    public static ExchangeRateResponse mapExchangeRateEntityToResponse(ExchangeRateRegisterEntity entity) {
        return ExchangeRateResponse.builder()
                .amount(entity.getConversionAmount())
                .baseAmount(entity.getBaseAmount())
                .baseCurrency(entity.getBaseCurrency())
                .targetCurrency(entity.getTargetCurrency())
                .targetRate(entity.getTargetRate())
                .build();
    }

    public static ExchangeRateInquiryResponse mapExchangeRateEntityToInquiryResponse(ExchangeRateRegisterEntity entity) {
        return ExchangeRateInquiryResponse.builder()
                .operationNumber(
                        leftPad(entity.getId().toString(), 8, '0'))
                .date(generateDateWithFormat(entity.getDateTime()))
                .user(entity.getUser())
                .baseAmount(entity.getBaseAmount())
                .currency(entity.getBaseCurrency())
                .targetRate(
                        mapExchangeRateEntityToTargetRateInquiry(entity))
                .build();
    }

    public static TargetRateInquiry mapExchangeRateEntityToTargetRateInquiry(ExchangeRateRegisterEntity entity) {
        return TargetRateInquiry.builder()
                .amount(entity.getConversionAmount())
                .rate(entity.getTargetRate())
                .currency(entity.getTargetCurrency())
                .build();
    }
}
