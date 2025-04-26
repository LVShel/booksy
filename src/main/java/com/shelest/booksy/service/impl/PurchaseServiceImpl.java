package com.shelest.booksy.service.impl;

import com.shelest.booksy.domain.dto.PurchasePreviewResponse;
import com.shelest.booksy.domain.dto.PurchaseRequest;
import com.shelest.booksy.domain.dto.PurchaseResponse;
import com.shelest.booksy.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    @Override
    public PurchasePreviewResponse preview(PurchaseRequest request) {
        return null;
    }

    @Override
    public PurchaseResponse purchase(PurchaseRequest request) {
        return null;
    }
}
