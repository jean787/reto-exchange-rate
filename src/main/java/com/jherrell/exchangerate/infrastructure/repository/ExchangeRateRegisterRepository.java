package com.jherrell.exchangerate.infrastructure.repository;

import com.jherrell.exchangerate.infrastructure.entity.ExchangeRateRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRegisterRepository extends
        JpaRepository<ExchangeRateRegisterEntity, Long> {

}
