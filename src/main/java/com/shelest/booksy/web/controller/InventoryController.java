package com.shelest.booksy.web.controller;

import com.shelest.booksy.domain.dto.BookCreateRequest;
import com.shelest.booksy.domain.dto.BookResponse;
import com.shelest.booksy.domain.dto.BookUpdateRequest;
import com.shelest.booksy.domain.model.enumeration.BookStatus;
import com.shelest.booksy.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final BookService bookService;

    @PostMapping("/books")
    public ResponseEntity<Void> addBook(@RequestBody @Valid BookCreateRequest request) {
        bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/books/{bookId}")
    public ResponseEntity<Void> updateBook(@PathVariable Long bookId, @RequestBody @Valid BookUpdateRequest request) {
        bookService.update(bookId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.delete(bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/{status}")
    public ResponseEntity<List<BookResponse>> getAllBooksByStatus(@PathVariable("status") BookStatus bookStatus) {
        return ResponseEntity.ok(bookService.getAllByStatus(bookStatus));
    }
}
