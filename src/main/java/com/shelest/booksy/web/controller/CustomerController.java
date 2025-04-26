package com.shelest.booksy.web.controller;

import com.shelest.booksy.domain.dto.LoyaltyResponse;
import com.shelest.booksy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{customerId}/loyalty")
    public ResponseEntity<LoyaltyResponse> getLoyaltyPoints(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getLoyaltyPoints(customerId));
    }
}
