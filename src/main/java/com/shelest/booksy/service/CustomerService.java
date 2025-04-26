package com.shelest.booksy.service;

import com.shelest.booksy.domain.dto.LoyaltyResponse;

public interface CustomerService {

    LoyaltyResponse getLoyaltyPoints(Long customerId);
}
