package com.shelest.booksy.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PurchasePreviewResponse {
    private Long customerId;
    private BigDecimal totalPrice;
    private BigDecimal discountApplied;
    private int loyaltyPointsEarned;
    private boolean loyaltyRewardApplied;
}
