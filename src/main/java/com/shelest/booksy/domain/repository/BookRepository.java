package com.shelest.booksy.domain.repository;

import com.shelest.booksy.domain.model.Book;
import com.shelest.booksy.domain.model.enumeration.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    List<Book> findAllByStatus(BookStatus status);
}
