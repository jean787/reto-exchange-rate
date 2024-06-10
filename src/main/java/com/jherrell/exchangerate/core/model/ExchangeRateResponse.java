package com.jherrell.exchangerate.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExchangeRateResponse {

    private BigDecimal amount;
    private BigDecimal baseAmount;
    private BigDecimal targetRate;
    private String baseCurrency;
    private String targetCurrency;
}
