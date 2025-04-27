package com.shelest.booksy.service.pricing;

import com.shelest.booksy.domain.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.shelest.booksy.domain.model.enumeration.BookStatus.ACTIVE;
import static com.shelest.booksy.domain.model.enumeration.BookType.NEW_RELEASE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NewReleasePricingStrategyTest {

    private NewReleasePricingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new NewReleasePricingStrategy();
    }

    @Test
    void calculatePrice_shouldReturnBasePriceRegardlessOfQuantity() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(49.99), NEW_RELEASE, 10, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal priceForOne = strategy.calculatePrice(book, 1);
        BigDecimal priceForMany = strategy.calculatePrice(book, 5);

        // Then
        assertThat(priceForOne).isEqualByComparingTo(BigDecimal.valueOf(49.99));
        assertThat(priceForMany).isEqualByComparingTo(BigDecimal.valueOf(49.99));
    }

    @Test
    void calculatePrice_shouldHandleZeroQuantityGracefully() {
        // Given
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(39.99), NEW_RELEASE, 10, LocalDateTime.MAX, ACTIVE);

        // When
        BigDecimal price = strategy.calculatePrice(book, 0);

        // Then
        assertThat(price).isEqualByComparingTo(BigDecimal.ZERO);
    }
}