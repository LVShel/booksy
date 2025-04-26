package com.shelest.booksy.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchasePreviewResponse {
    private Long customerId;
    private BigDecimal totalPrice;
    private BigDecimal discountApplied;
    private int loyaltyPointsEarned;
    private boolean loyaltyRewardApplied;
}
