package com.shelest.booksy.domain.dto;

import com.shelest.booksy.domain.model.enumeration.BookType;
import com.shelest.booksy.service.validation.ValidIsbn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookCreateRequest {
    @ValidIsbn
    private String isbn;
    @NotBlank
    private String title;
    @NotNull
    private BigDecimal basePrice;
    @NotNull
    private BookType bookType;
    @PositiveOrZero
    private int stock;
}
