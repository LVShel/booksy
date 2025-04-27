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
import com.shelest.booksy.service.PurchaseService;
import com.shelest.booksy.service.config.LoyaltyConfig;
import com.shelest.booksy.service.exception.PurchaseInvalidException;
import com.shelest.booksy.service.pricing.PricingStrategy;
import com.shelest.booksy.service.pricing.PricingStrategyFactory;
import com.shelest.booksy.service.utils.PriceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.shelest.booksy.domain.model.enumeration.BookType.OLD_EDITION;
import static com.shelest.booksy.domain.model.enumeration.BookType.REGULAR;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final BookRepository bookRepository;
    private final LoyaltyService loyaltyService;
    private final LoyaltyConfig loyaltyConfig;
    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;

    @Override
    public PurchasePreviewResponse preview(PurchasePreviewRequest request) {
        log.info("Previewing purchase for customerId:{} with bookIds:{}", request.getCustomerId(), request.getBookIds());

        List<Book> books = bookRepository.findAllById(request.getBookIds());
        validateBooksForPurchase(request.getBookIds(), books);

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Customer was not found by Id: " + request.getCustomerId()));

        return calculatePurchasePreview(customer.getId(), books);
    }

    @Transactional
    @Override
    public PurchaseResponse purchase(PurchaseRequest request) {
        log.info("Processing purchase for customerId:{} with bookIds:{}", request.getCustomerId(), request.getBookIds());

        List<Book> books = bookRepository.findAllById(request.getBookIds());
        validateBooksForPurchase(request.getBookIds(), books);

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Customer was not found by Id: " + request.getCustomerId()));

        PurchasePreviewResponse previewResponse = calculatePurchasePreview(customer.getId(), books);

        if (purchaseConditionsChanged(request, previewResponse)) {
            log.warn("Purchase conditions mismatch detected for customerId={}", customer.getId());
            throw new PurchaseInvalidException("Agreed purchase conditions are not valid anymore. Please review your purchase!");
        }

        log.info("Purchase conditions valid. Proceeding to create purchase for customerId={}", customer.getId());

        Purchase purchase = createPurchase(customer, books, request, previewResponse);

        updateStock(books);

        updateLoyalty(customer, request.isLoyaltyRewardApplied(), previewResponse.getLoyaltyPointsEarned());

        return PurchaseResponse.builder()
                .purchaseId(purchase.getId())
                .totalPrice(purchase.getTotalPrice())
                .loyaltyPointsEarned(purchase.getLoyaltyPointsEarned())
                .build();
    }

    private boolean purchaseConditionsChanged(PurchaseRequest request, PurchasePreviewResponse previewResponse) {
        return request.getAgreedTotalPrice().compareTo(previewResponse.getTotalPrice()) != 0
                || request.isLoyaltyRewardApplied() != previewResponse.isLoyaltyRewardApplied();
    }

    private PurchasePreviewResponse calculatePurchasePreview(Long customerId, List<Book> books) {
        BigDecimal priceNoDiscount = PriceUtils.round(calculateTotalBasePrice(books));
        BigDecimal discountedPrice = PriceUtils.round(calculateTotalDiscountedPrice(books));

        boolean loyaltyApplied = false;
        int loyaltyEarned = 0;

        if (loyaltyConfig.isLoyaltyEnabled()) {
            loyaltyEarned = loyaltyService.calculateLoyaltyPointsForPurchase(customerId, books);
            if (loyaltyEarned >= loyaltyConfig.getMaxWithdrawThreshold()) {
                Optional<Book> cheapestEligibleBook = books.stream()
                        .filter(book -> Set.of(REGULAR, OLD_EDITION).contains(book.getBookType()))
                        .min(Comparator.comparing(Book::getBasePrice));
                if (cheapestEligibleBook.isPresent()) {
                    // yes, if we are to give away a book for free, it has to be the cheapest one. This is how we do business
                    discountedPrice = PriceUtils.round(discountedPrice.subtract(cheapestEligibleBook.get().getBasePrice()));
                    loyaltyApplied = true;
                }
            }
        }

        BigDecimal discountApplied = PriceUtils.round(priceNoDiscount.subtract(discountedPrice));

        return PurchasePreviewResponse.builder()
                .customerId(customerId)
                .discountApplied(discountApplied)
                .loyaltyPointsEarned(loyaltyEarned)
                .loyaltyRewardApplied(loyaltyApplied)
                .totalPrice(discountedPrice)
                .build();
    }

    private BigDecimal calculateTotalBasePrice(List<Book> books) {
        return books.stream()
                .map(Book::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalDiscountedPrice(List<Book> books) {
        int quantity = books.size();
        return books.stream()
                .map(book -> {
                    PricingStrategy strategy = PricingStrategyFactory.getStrategy(book.getBookType());
                    return strategy.calculatePrice(book, quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateBooksForPurchase(Set<Long> requestedIds, List<Book> availableBooks) {
        final boolean anyOutOfStock = availableBooks.stream().anyMatch(book -> book.getStock() < 1);
        if (anyOutOfStock || (availableBooks.size() != requestedIds.size())) {
            log.warn("Validation failed: some books requested are unavailable or out of stock. Requested: {}, Found: {}", requestedIds, availableBooks.size());
            throw new PurchaseInvalidException("Some selected books are not available for purchase. Please review selection!");
        }
    }

    private Purchase createPurchase(Customer customer, List<Book> books, PurchaseRequest request, PurchasePreviewResponse previewResponse) {
        Purchase purchase = Purchase.builder()
                .customer(customer)
                .books(books)
                .totalPrice(request.getAgreedTotalPrice())
                .loyaltyRewardApplied(request.isLoyaltyRewardApplied())
                .loyaltyPointsEarned(previewResponse.getLoyaltyPointsEarned())
                .build();
        return purchaseRepository.save(purchase);
    }

    private void updateStock(List<Book> books) {
        for (Book book : books) {
            book.setStock(book.getStock() - 1);
        }
        bookRepository.saveAll(books);
    }

    private void updateLoyalty(Customer customer, boolean loyaltyRewardApplied, int loyaltyPointsEarned) {
        customer.setLoyaltyPoints(loyaltyRewardApplied ? 0 : loyaltyPointsEarned);
        customerRepository.save(customer);
    }
}
