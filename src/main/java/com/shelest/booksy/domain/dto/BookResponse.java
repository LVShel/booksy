package com.shelest.booksy.domain.dto;

import com.shelest.booksy.domain.model.enumeration.BookStatus;
import com.shelest.booksy.domain.model.enumeration.BookType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private BigDecimal basePrice;
    private BookType bookType;
    private BookStatus status;
    private int stock;
}
