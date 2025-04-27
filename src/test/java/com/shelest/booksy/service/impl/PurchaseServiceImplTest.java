package com.shelest.booksy.service.impl;

import com.shelest.booksy.domain.dto.PurchasePreviewRequest;
import com.shelest.booksy.domain.dto.PurchasePreviewResponse;
import com.shelest.booksy.domain.dto.PurchaseRequest;
import com.shelest.booksy.domain.dto.PurchaseResponse;
import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.domain.model.Customer;
import com.shelest.booksy.domain.model.Purchase;
import com.shelest.booksy.domain.repository.BookRepository;
import com.shelest.booksy.domain.repository.CustomerRepository;
import com.shelest.booksy.domain.repository.PurchaseRepository;
import com.shelest.booksy.service.LoyaltyService;
import com.shelest.booksy.service.config.LoyaltyConfig;
import com.shelest.booksy.service.exception.PurchaseInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.shelest.booksy.domain.model.enumeration.BookStatus.ACTIVE;
import static com.shelest.booksy.domain.model.enumeration.BookType.NEW_RELEASE;
import static com.shelest.booksy.domain.model.enumeration.BookType.REGULAR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchaseServiceImplTest {

    private BookRepository bookRepository;
    private LoyaltyService loyaltyService;
    private LoyaltyConfig loyaltyConfig;
    private CustomerRepository customerRepository;
    private PurchaseRepository purchaseRepository;

    private PurchaseServiceImpl purchaseService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        loyaltyService = mock(LoyaltyService.class);
        customerRepository = mock(CustomerRepository.class);
        purchaseRepository = mock(PurchaseRepository.class);
        loyaltyConfig = mock(LoyaltyConfig.class);

        purchaseService = new PurchaseServiceImpl(bookRepository, loyaltyService, loyaltyConfig, customerRepository, purchaseRepository);
    }

    @Test
    void preview_shouldCalculateSuccessfully() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L);
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 5);

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);

        PurchasePreviewRequest request = new PurchasePreviewRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);

        // When
        PurchasePreviewResponse response = purchaseService.preview(request);

        // Then
        assertThat(response.getCustomerId()).isEqualTo(customerId);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertThat(response.isLoyaltyRewardApplied()).isFalse();
    }

    @Test
    void preview_shouldCalculateDiscountOnRegularNoLoyaltyEarned() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L, 2L, 3L);
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(20), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Book book2 = new Book(2L, "1234", "Test", BigDecimal.valueOf(30), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Book book3 = new Book(3L, "12345", "Test", BigDecimal.valueOf(40), NEW_RELEASE, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 5);

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book, book2, book3));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);
        when(loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book, book2, book3))).thenReturn(8);

        PurchasePreviewRequest request = new PurchasePreviewRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);

        // When
        PurchasePreviewResponse response = purchaseService.preview(request);

        // Then
        assertThat(response.getCustomerId()).isEqualTo(customerId);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(85));
        assertThat(response.getDiscountApplied()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(response.getLoyaltyPointsEarned()).isEqualTo(8);
        assertThat(response.isLoyaltyRewardApplied()).isFalse();
    }

    @Test
    void preview_shouldCalculateDiscountOnRegularPusLoyalty() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L, 2L, 3L);
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(20), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Book book2 = new Book(2L, "1234", "Test", BigDecimal.valueOf(30), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Book book3 = new Book(3L, "12345", "Test", BigDecimal.valueOf(40), NEW_RELEASE, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 8);

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book, book2, book3));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);
        when(loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book, book2, book3))).thenReturn(11);

        PurchasePreviewRequest request = new PurchasePreviewRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);

        // When
        PurchasePreviewResponse response = purchaseService.preview(request);

        // Then
        assertThat(response.getCustomerId()).isEqualTo(customerId);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(65));
        assertThat(response.getDiscountApplied()).isEqualByComparingTo(BigDecimal.valueOf(25));
        assertThat(response.getLoyaltyPointsEarned()).isEqualTo(11);
        assertThat(response.isLoyaltyRewardApplied()).isTrue();
    }

    @Test
    void preview_shouldThrow_whenBookOutOfStock() {
        // Given
        Long customerId = 1L;
        Set<Long> requestedBookIds = Set.of(1L);
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), REGULAR, 0, LocalDateTime.MAX, ACTIVE);

        when(bookRepository.findAllById(requestedBookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer.builder().id(customerId).build()));

        PurchasePreviewRequest request = new PurchasePreviewRequest();
        request.setCustomerId(customerId);
        request.setBookIds(requestedBookIds);

        // When & Then
        assertThatThrownBy(() -> purchaseService.preview(request))
                .isInstanceOf(PurchaseInvalidException.class)
                .hasMessageContaining("not available for purchase");
    }

    @Test
    void preview_shouldThrow_whenBookUnavailable() {
        // Given
        Long customerId = 1L;
        Set<Long> requestedBookIds = Set.of(1L, 2L);
        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), REGULAR, 2, LocalDateTime.MAX, ACTIVE);

        when(bookRepository.findAllById(requestedBookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(Customer.builder().id(customerId).build()));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);

        PurchasePreviewRequest request = new PurchasePreviewRequest();
        request.setCustomerId(customerId);
        request.setBookIds(requestedBookIds);

        // When & Then
        assertThatThrownBy(() -> purchaseService.preview(request))
                .isInstanceOf(PurchaseInvalidException.class)
                .hasMessageContaining("not available for purchase");
    }

    @Test
    void purchase_shouldCreatePurchaseSuccessfully_AndUpdateStock_AndWithdrawLoyalty() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L);

        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 9);
        Purchase savedPurchase = Purchase.builder().id(99L).build();

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);
        when(purchaseRepository.save(Mockito.any(Purchase.class))).thenReturn(savedPurchase);
        when(loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book))).thenReturn(10);

        PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);
        request.setAgreedTotalPrice(BigDecimal.valueOf(0));// expected to give out for free due to loyalty
        request.setLoyaltyRewardApplied(true);

        // When
        PurchaseResponse response = purchaseService.purchase(request);

        // Then
        assertThat(response.getPurchaseId()).isEqualTo(99L);
        assertThat(customer.getLoyaltyPoints()).isEqualTo(0); // loyalty set to zero
        assertThat(book.getStock()).isEqualTo(4); // stock decremented
        verify(bookRepository, times(1)).saveAll(anyList());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void purchase_shouldCreatePurchaseSuccessfully_AndDecreaseStock_increaseLoyalty() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L);

        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 5);
        Purchase savedPurchase = Purchase.builder().id(99L).build();

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);
        when(purchaseRepository.save(Mockito.any(Purchase.class))).thenReturn(savedPurchase);
        when(loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book))).thenReturn(6);

        PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);
        request.setAgreedTotalPrice(BigDecimal.valueOf(30));// no discount
        request.setLoyaltyRewardApplied(false);// loyalty does not hit threshold

        // When
        PurchaseResponse response = purchaseService.purchase(request);

        // Then
        assertThat(response.getPurchaseId()).isEqualTo(99L);
        assertThat(customer.getLoyaltyPoints()).isEqualTo(6); // loyalty increased
        assertThat(book.getStock()).isEqualTo(4); // stock decremented
        verify(bookRepository, times(1)).saveAll(anyList());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void purchase_shouldThrow_whenPriceMismatch() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L);

        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(35), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 5);
        Purchase savedPurchase = Purchase.builder().id(99L).build();

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);
        when(purchaseRepository.save(Mockito.any(Purchase.class))).thenReturn(savedPurchase);
        when(loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book))).thenReturn(6);

        PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);
        request.setAgreedTotalPrice(BigDecimal.valueOf(30));// in the meantime price has increased since last preview
        request.setLoyaltyRewardApplied(false);// loyalty does not hit threshold

        // When & Then
        assertThatThrownBy(() -> purchaseService.purchase(request))
                .isInstanceOf(PurchaseInvalidException.class)
                .hasMessageContaining("conditions are not valid anymore");
    }

    @Test
    void purchase_shouldThrow_whenLoyaltyAwardMismatch() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L);

        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), REGULAR, 5, LocalDateTime.MAX, ACTIVE);
        Customer customer = new Customer(customerId, "John Doe", 9);
        Purchase savedPurchase = Purchase.builder().id(99L).build();

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(false);// loyalty disabled since last preview
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);
        when(purchaseRepository.save(Mockito.any(Purchase.class))).thenReturn(savedPurchase);
        when(loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book))).thenReturn(9);// no loyalty accumulation since disabled

        PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);
        request.setAgreedTotalPrice(BigDecimal.valueOf(0));// expected when loyalty was on during last preview
        request.setLoyaltyRewardApplied(true);// expected when loyalty was on during last preview

        // When & Then
        assertThatThrownBy(() -> purchaseService.purchase(request))
                .isInstanceOf(PurchaseInvalidException.class)
                .hasMessageContaining("conditions are not valid anymore");
    }

    @Test
    void purchase_shouldThrow_whenBookOutOfStock() {
        // Given
        Long customerId = 1L;
        Set<Long> bookIds = Set.of(1L);

        Book book = new Book(1L, "123", "Test", BigDecimal.valueOf(30), REGULAR, 0, LocalDateTime.MAX, ACTIVE); // out of stock
        Customer customer = new Customer(customerId, "John Doe", 5);
        Purchase savedPurchase = Purchase.builder().id(99L).build();

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loyaltyConfig.isLoyaltyEnabled()).thenReturn(true);
        when(loyaltyConfig.getMaxWithdrawThreshold()).thenReturn(10);
        when(purchaseRepository.save(Mockito.any(Purchase.class))).thenReturn(savedPurchase);
        when(loyaltyService.calculateLoyaltyPointsForPurchase(customerId, List.of(book))).thenReturn(6);

        PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId);
        request.setBookIds(bookIds);
        request.setAgreedTotalPrice(BigDecimal.valueOf(30));// no discount
        request.setLoyaltyRewardApplied(false);// loyalty does not hit threshold

        // When & Then
        assertThatThrownBy(() -> purchaseService.purchase(request))
                .isInstanceOf(PurchaseInvalidException.class)
                .hasMessageContaining("books are not available for purchase");
    }
}