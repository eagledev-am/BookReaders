package com.eagledev.bookreaders.services.payment;

import com.eagledev.bookreaders.entities.Order;
import com.eagledev.bookreaders.entities.PaymentTransaction;
import com.eagledev.bookreaders.services.payment.provider.PaymentFactory;
import com.eagledev.bookreaders.services.payment.provider.PaymentProvider;
import com.eagledev.bookreaders.repos.OrderRepo;
import com.eagledev.bookreaders.repos.PaymentTransactionRepo;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepo orderRepo;
    private final PaymentTransactionRepo transactionRepo;
    private final PaymentFactory paymentFactory;

    @Override
    @Transactional
    public PaymentTransaction processPayment(UUID orderUuid) {
        Order order = orderRepo.findByUuid(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "uuid", orderUuid));

        PaymentProvider provider = paymentFactory.getProvider(order.getPaymentMethod());
        PaymentTransaction transaction = provider.initializePayment(order);
        transaction.setOrder(order);

        return transactionRepo.save(transaction);
    }
}

