package com.shelest.booksy.service;

import com.shelest.booksy.domain.dto.BookCreateRequest;
import com.shelest.booksy.domain.dto.BookResponse;
import com.shelest.booksy.domain.dto.BookUpdateRequest;
import com.shelest.booksy.domain.model.enumeration.BookStatus;

import java.util.List;

public interface BookService {

    void create(BookCreateRequest request);

    void update(Long bookId, BookUpdateRequest request);

    void delete(Long bookId);

    List<BookResponse> getAllActive();

    List<BookResponse> getAllByStatus(BookStatus bookStatus);
}
