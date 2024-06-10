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
public class TargetRateInquiry {

    private BigDecimal amount;
    private String currency;
    private BigDecimal rate;
}
