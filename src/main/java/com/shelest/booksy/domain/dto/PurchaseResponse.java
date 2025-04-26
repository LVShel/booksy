package com.shelest.booksy.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseResponse {
    private Long purchaseId;
    private BigDecimal totalPrice;
    private int loyaltyPointsEarned;
}
