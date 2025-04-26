package com.shelest.booksy.domain.dto;

import lombok.Data;

@Data
public class LoyaltyResponse {
    private Long customerId;
    private int loyaltyPoints;
}
