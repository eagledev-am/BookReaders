package com.eagledev.bookreaders.services.payment;

import com.eagledev.bookreaders.entities.PaymentTransaction;

import java.util.UUID;

public interface PaymentService {
    PaymentTransaction processPayment(UUID orderUuid);
}

