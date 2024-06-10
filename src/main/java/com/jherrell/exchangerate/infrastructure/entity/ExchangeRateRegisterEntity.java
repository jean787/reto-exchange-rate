package com.jherrell.exchangerate.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "exchange_rate")
@Data
@Builder
public class ExchangeRateRegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time")
    private Date dateTime;

    @Column(name = "user_id")
    private String user;

    @Column(name = "base_amount")
    private BigDecimal baseAmount;

    @Column(name = "conversion_amount")
    private BigDecimal conversionAmount;

    @Column(name = "base_currency")
    private String baseCurrency;

    @Column(name = "target_currency")
    private String targetCurrency;

    @Column(name = "target_rate")
    private BigDecimal targetRate;


}
