package com.shelest.booksy.service;

import com.shelest.booksy.domain.dto.PurchasePreviewResponse;
import com.shelest.booksy.domain.dto.PurchaseRequest;
import com.shelest.booksy.domain.dto.PurchaseResponse;

public interface PurchaseService {

    PurchasePreviewResponse preview(PurchaseRequest request);

    PurchaseResponse purchase(PurchaseRequest request);
}
