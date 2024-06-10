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
public class ExchangeRateInquiryResponse {

    private String operationNumber;
    private String date;
    private String user;
    private BigDecimal baseAmount;
    private String currency;
    private TargetRateInquiry targetRate;

}
