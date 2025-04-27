package com.shelest.booksy.service.pricing;

import com.shelest.booksy.domain.model.Book;

import java.math.BigDecimal;

public class NewReleasePricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Book book, int quantity) {
        if (quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return book.getBasePrice(); // No discount here
    }
}
