package com.shelest.booksy.service.mapper;

import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.domain.dto.BookCreateRequest;
import com.shelest.booksy.domain.dto.BookUpdateRequest;
import com.shelest.booksy.domain.dto.BookResponse;

public class BookMapper {

    public static Book toEntity(BookCreateRequest request) {
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setBasePrice(request.getBasePrice());
        book.setBookType(request.getBookType());
        book.setStock(request.getStock());
        return book;
    }

    public static Book toEntity(BookUpdateRequest request, Book book) {
        // Updating an existing book
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setBasePrice(request.getBasePrice());
        book.setBookType(request.getBookType());
        book.setStock(request.getStock());
        return book;
    }

    public static BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setBasePrice(book.getBasePrice());
        response.setBookType(book.getBookType());
        response.setStock(book.getStock());
        return response;
    }
}
