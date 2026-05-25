package com.eagledev.bookreaders.services.payment.provider;

import com.eagledev.bookreaders.entities.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFactory {
    private final CashOnDeliveryProviderImpl cashOnDeliveryProvider;
    private final MockOnlinePaymentProviderImpl mockOnlinePaymentProvider;

    public PaymentProvider getProvider(PaymentMethod method) {
        if (method == null) {
            return cashOnDeliveryProvider;
        }
        return switch (method) {
            case ONLINE -> mockOnlinePaymentProvider;
            case COD -> cashOnDeliveryProvider;
        };
    }
}

