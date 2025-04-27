package com.shelest.booksy.service.impl;

import com.shelest.booksy.domain.dto.BookCreateRequest;
import com.shelest.booksy.domain.dto.BookResponse;
import com.shelest.booksy.domain.dto.BookUpdateRequest;
import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.domain.model.enumeration.BookStatus;
import com.shelest.booksy.domain.repository.BookRepository;
import com.shelest.booksy.service.BookService;
import com.shelest.booksy.service.exception.DuplicateBookException;
import com.shelest.booksy.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public void create(BookCreateRequest request) {
        final Optional<Book> bookByIsbn = bookRepository.findByIsbn(request.getIsbn());
        if (bookByIsbn.isPresent()) {
            throw new DuplicateBookException("Book already exists with isbn: " + request.getIsbn());
        }
        log.info("Creating new book with ISBN: {}", request.getIsbn());
        bookRepository.save(BookMapper.toEntity(request));
    }

    @Override
    public void update(Long bookId, BookUpdateRequest request) {
        log.info("Updating book with ID: {}", bookId);
        final Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book was not found by Id: " + bookId));
        bookRepository.save(BookMapper.toEntity(request, book));
    }

    @Override
    public void delete(Long bookId) {
        log.info("Deleting book with ID: {}", bookId);
        final Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book was not found by Id: " + bookId));
        bookRepository.delete(book);
    }

    @Override
    public List<BookResponse> getAllActive() {
        final List<Book> books = bookRepository.findAllByStatus(BookStatus.ACTIVE);
        return books.stream().map(BookMapper::toResponse).toList();
    }

    @Override
    public List<BookResponse> getAllByStatus(BookStatus bookStatus) {
        final List<Book> books = bookRepository.findAllByStatus(bookStatus);
        return books.stream().map(BookMapper::toResponse).toList();
    }
}
