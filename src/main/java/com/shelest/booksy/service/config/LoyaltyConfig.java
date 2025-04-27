package com.shelest.booksy.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "booksy.loyalty")
public class LoyaltyConfig {

    private boolean loyaltyEnabled;
    private int maxWithdrawThreshold;
}
