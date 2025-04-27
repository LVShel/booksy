package com.shelest.booksy.web.controller;

import com.shelest.booksy.domain.dto.PurchasePreviewResponse;
import com.shelest.booksy.domain.dto.PurchasePreviewRequest;
import com.shelest.booksy.domain.dto.PurchaseRequest;
import com.shelest.booksy.domain.dto.PurchaseResponse;
import com.shelest.booksy.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/preview")
    public ResponseEntity<PurchasePreviewResponse> preview(@RequestBody @Valid PurchasePreviewRequest request) {
        return ResponseEntity.ok(purchaseService.preview(request));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> makePurchase(@RequestBody @Valid PurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.purchase(request));
    }
}
