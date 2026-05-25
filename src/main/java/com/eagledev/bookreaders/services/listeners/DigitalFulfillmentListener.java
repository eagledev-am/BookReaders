package com.eagledev.bookreaders.services.listeners;

import com.eagledev.bookreaders.dtos.events.PaymentCompletedEvent;
import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.services.tracking.progress.ReadingProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigitalFulfillmentListener {

    private final ReadingProgressService digitalLibraryService;

    @RabbitListener(queues = RabbitMqConfig.COMMERCE_PAYMENT_FULFILLMENT_QUEUE)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        digitalLibraryService.grantDigitalAccess(event.userUuid(), event.bookUuids());
    }
}
