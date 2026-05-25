package com.eagledev.bookreaders.services.listeners;

import com.eagledev.bookreaders.dtos.events.PaymentCompletedEvent;
import com.eagledev.bookreaders.services.email.CommerceEmailService;
import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.repos.UserRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptEmailListener {

    private final CommerceEmailService emailService;
    private final UserRepo userRepo;

    @RabbitListener(queues = RabbitMqConfig.COMMERCE_PAYMENT_RECEIPT_QUEUE)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        User user = userRepo.findUserByUuid(event.userUuid())
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", event.userUuid()));

        String subject = "Your BookReaders receipt";
        String body = "Thanks for your purchase. Order: " + event.orderUuid() + "\n Total: " + event.totalAmount();

        try {
            emailService.sendReceipt(user.getEmail(), subject, body);
        } catch (MessagingException ex) {
            log.warn("Failed to send receipt email: {}", ex.getMessage());
        }
    }
}
