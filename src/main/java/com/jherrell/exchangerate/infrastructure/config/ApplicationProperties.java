package com.jherrell.exchangerate.infrastructure.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties
@Configuration
@Getter
public class ApplicationProperties {

    @Value("${jwt.secret}")
    private String secret;
}
