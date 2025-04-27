package com.shelest.booksy.service.pricing;

import com.shelest.booksy.domain.model.Book;
import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Book book, int quantity);
}
