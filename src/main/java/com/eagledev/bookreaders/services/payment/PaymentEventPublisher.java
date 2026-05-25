package com.eagledev.bookreaders.services.payment;

import com.eagledev.bookreaders.dtos.events.PaymentCompletedEvent;
import com.eagledev.bookreaders.config.RabbitMqConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {
    private final AmqpTemplate template;

    public PaymentEventPublisher(@Qualifier("amqpTemplate") AmqpTemplate template) {
        this.template = template;
    }

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
        this.template.convertAndSend(
                RabbitMqConfig.COMMERCE_PAYMENT_EXCHANGE,
                RabbitMqConfig.COMMERCE_PAYMENT_ROUTING_KEY,
                event
        );
    }

    public void publishPaymentCancelledEvent(PaymentCompletedEvent event) {
        this.template.convertAndSend(
                RabbitMqConfig.COMMERCE_PAYMENT_EXCHANGE,
                RabbitMqConfig.COMMERCE_PAYMENT_CANCELLED_ROUTING_KEY,
                event
        );
    }
}

