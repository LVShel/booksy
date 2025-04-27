package com.shelest.booksy.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class PurchasePreviewRequest {
    @NotNull
    private Long customerId;
    @NotEmpty
    private Set<Long> bookIds;
}
