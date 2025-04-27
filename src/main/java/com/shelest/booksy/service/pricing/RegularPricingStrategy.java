package com.shelest.booksy.service.pricing;

import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.service.utils.PriceUtils;

import java.math.BigDecimal;

public class RegularPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Book book, int quantity) {
        if (quantity <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal price = book.getBasePrice();
        if (quantity >= 3) {
            price = price.multiply(BigDecimal.valueOf(0.9)); // 10% discount
        }
        return PriceUtils.round(price);
    }
}
