package com.shelest.booksy.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class PurchaseRequest {
    @NotNull
    private Long customerId;
    @NotEmpty
    private Set<Long> bookIds;
    @NotNull
    private BigDecimal agreedTotalPrice;
    private boolean loyaltyRewardApplied;
}
