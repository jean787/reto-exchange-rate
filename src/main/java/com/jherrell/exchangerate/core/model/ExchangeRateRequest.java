package com.jherrell.exchangerate.core.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExchangeRateRequest {

    @NotNull
    @Valid
    @DecimalMin("0")
    private BigDecimal amount;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{3}")
    private String sourceCurrency;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{3}")
    private String targetCurrency;

}
