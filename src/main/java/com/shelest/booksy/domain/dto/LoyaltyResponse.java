package com.shelest.booksy.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoyaltyResponse {
    private Long customerId;
    private int loyaltyPoints;
}
