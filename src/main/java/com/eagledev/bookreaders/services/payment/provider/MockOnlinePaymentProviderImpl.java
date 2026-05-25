package com.eagledev.bookreaders.services.payment.provider;

import com.eagledev.bookreaders.entities.Order;
import com.eagledev.bookreaders.entities.PaymentTransaction;
import com.eagledev.bookreaders.entities.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockOnlinePaymentProviderImpl implements PaymentProvider {

    @Override
    public PaymentTransaction initializePayment(Order order) {
        return PaymentTransaction.builder()
                .providerName("MOCK_ONLINE")
                .transactionReference("MOCK-" + UUID.randomUUID())
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Override
    public PaymentStatus verifyPayment(String transactionRef) {
        return PaymentStatus.PENDING;
    }
}

