package com.shelest.booksy.service.impl;

import com.shelest.booksy.domain.dto.LoyaltyResponse;
import com.shelest.booksy.domain.model.Customer;
import com.shelest.booksy.domain.repository.CustomerRepository;
import com.shelest.booksy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public LoyaltyResponse getLoyaltyPoints(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer was not found by Id: " + customerId));

        return LoyaltyResponse.builder()
                .customerId(customer.getId())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .build();
    }
}
