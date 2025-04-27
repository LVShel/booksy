package com.shelest.booksy.service.impl;

import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.domain.model.Customer;
import com.shelest.booksy.domain.repository.CustomerRepository;
import com.shelest.booksy.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {

    private final CustomerRepository customerRepository;

    @Override
    public int calculateLoyaltyPointsForPurchase(Long customerId, List<Book> booksInCart) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer was not found by Id: " + customerId));
        int loyaltyPoints = customer.getLoyaltyPoints();
        if (!CollectionUtils.isEmpty(booksInCart)) {
            loyaltyPoints += booksInCart.size();
        }

        return loyaltyPoints;
    }
}
