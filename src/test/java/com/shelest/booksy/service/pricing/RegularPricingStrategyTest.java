package com.shelest.booksy.service.pricing;

import com.shelest.booksy.domain.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.shelest.booksy.domain.model.enumeration.BookStatus.ACTIVE;
import static com.shelest.booksy.domain.model.enumeration.BookType.REGULAR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RegularPricingStrategyTest {
    private RegularPricingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new RegularPricingStrategy();
    }

    @Test
    void calculatePrice_shouldApplyBaseDiscount_whenLessThanThreeBooks() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(50.00), REGULAR, 10, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal price = strategy.calculatePrice(book, 2);

        // Then
        assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(50.00)); // no discount if less than 3
    }

    @Test
    void calculatePrice_shouldApplyBaseAndBundleDiscount_whenThreeOrMoreBooks() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(40.00), REGULAR, 10, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal price = strategy.calculatePrice(book, 3);

        // Then
        BigDecimal expectedPrice = BigDecimal.valueOf(40.00)
                .multiply(BigDecimal.valueOf(0.9));   // Base 10% discount
        assertThat(price).isEqualByComparingTo(expectedPrice);
    }

    @Test
    void calculatePrice_shouldHandleZeroQuantityGracefully() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30.00), REGULAR, 10, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal price = strategy.calculatePrice(book, 0);

        // Then
        assertThat(price).isEqualByComparingTo(BigDecimal.ZERO);
    }

}