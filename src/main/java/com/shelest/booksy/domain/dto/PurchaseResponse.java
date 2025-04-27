package com.shelest.booksy.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PurchaseResponse {
    private Long purchaseId;
    private BigDecimal totalPrice;
    private int loyaltyPointsEarned;
}
