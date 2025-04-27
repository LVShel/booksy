package com.shelest.booksy.service.impl;

import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.domain.model.Customer;
import com.shelest.booksy.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.shelest.booksy.domain.model.enumeration.BookStatus.ACTIVE;
import static com.shelest.booksy.domain.model.enumeration.BookType.NEW_RELEASE;
import static com.shelest.booksy.domain.model.enumeration.BookType.REGULAR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoyaltyServiceImplTest {

    private CustomerRepository customerRepository;
    private LoyaltyServiceImpl loyaltyService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);

        loyaltyService = new LoyaltyServiceImpl(customerRepository);
    }

    @Test
    void preview_shouldCalculateSuccessfully() {
        // Given
        Long customerId = 1L;
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(20), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Book book2 = new Book(2L, "1234", "Test", BigDecimal.valueOf(30), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Book book3 = new Book(3L, "12345", "Test", BigDecimal.valueOf(40), NEW_RELEASE, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 5);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        int result = loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book, book2, book3));

        // Then
        assertThat(result).isEqualTo(8);
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void preview_shouldHandleEmptyBooksGracefully() {
        // Given
        Long customerId = 1L;

        Customer customer = new Customer(customerId, "John Doe", 5);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        int result = loyaltyService.calculateLoyaltyPointsForPurchase(customerId, null);

        // Then
        assertThat(result).isEqualTo(5);
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void preview_shouldThrowIfCustomerNotFound() {
        // Given
        Long customerId = 1L;

        Customer customer = new Customer(customerId, "John Doe", 5);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loyaltyService.calculateLoyaltyPointsForPurchase(customerId, null))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Customer was not found by Id");
    }

}