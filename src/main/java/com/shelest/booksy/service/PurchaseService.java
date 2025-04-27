package com.shelest.booksy.service;

import com.shelest.booksy.domain.dto.PurchasePreviewResponse;
import com.shelest.booksy.domain.dto.PurchasePreviewRequest;
import com.shelest.booksy.domain.dto.PurchaseRequest;
import com.shelest.booksy.domain.dto.PurchaseResponse;

public interface PurchaseService {

    PurchasePreviewResponse preview(PurchasePreviewRequest request);

    PurchaseResponse purchase(PurchaseRequest request);
}
