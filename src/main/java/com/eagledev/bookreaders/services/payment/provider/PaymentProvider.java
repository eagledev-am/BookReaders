package com.eagledev.bookreaders.services.payment.provider;

import com.eagledev.bookreaders.entities.Order;
import com.eagledev.bookreaders.entities.PaymentTransaction;
import com.eagledev.bookreaders.entities.enums.PaymentStatus;

public interface PaymentProvider {
    PaymentTransaction  initializePayment(Order order);
    PaymentStatus verifyPayment(String transactionRef);
}

