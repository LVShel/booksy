package com.shelest.booksy.service.pricing;

import com.shelest.booksy.domain.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.shelest.booksy.domain.model.enumeration.BookStatus.ACTIVE;
import static com.shelest.booksy.domain.model.enumeration.BookType.OLD_EDITION;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OldEditionPricingStrategyTest {

    private OldEditionPricingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new OldEditionPricingStrategy();
    }

    @Test
    void calculatePrice_shouldApplyBaseDiscount_whenLessThanThreeBooks() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(50.00), OLD_EDITION, 10, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal price = strategy.calculatePrice(book, 2);

        // Then
        BigDecimal expectedPrice = BigDecimal.valueOf(50.00)
                .multiply(BigDecimal.valueOf(0.8));
        assertThat(price).isEqualByComparingTo(expectedPrice);
    }

    @Test
    void calculatePrice_shouldApplyBaseAndBundleDiscount_whenThreeOrMoreBooks() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(40.00), OLD_EDITION, 15, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal price = strategy.calculatePrice(book, 3);

        // Then
        BigDecimal expectedPrice = BigDecimal.valueOf(40.00)
                .multiply(BigDecimal.valueOf(0.8))   // Base 20% discount
                .multiply(BigDecimal.valueOf(0.95)); // Additional 5% discount
        assertThat(price).isEqualByComparingTo(expectedPrice);
    }

    @Test
    void calculatePrice_shouldHandleZeroQuantityGracefully() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), OLD_EDITION, 5, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal price = strategy.calculatePrice(book, 0);

        // Then
        assertThat(price).isEqualByComparingTo(BigDecimal.ZERO);
    }

}