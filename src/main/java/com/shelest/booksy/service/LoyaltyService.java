package com.shelest.booksy.service;

import com.shelest.booksy.domain.model.Book;

import java.util.List;

public interface LoyaltyService {

    int calculateLoyaltyPointsForPurchase(Long customerId, List<Book> booksInCart);
}
