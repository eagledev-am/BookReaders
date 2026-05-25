package com.eagledev.bookreaders.services.listeners;

import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.dtos.events.PaymentCompletedEvent;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.repos.UserRepo;
import com.eagledev.bookreaders.services.email.CommerceEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelEmailListener {
    private final CommerceEmailService emailService;
    private final UserRepo userRepo;

    @RabbitListener(queues = RabbitMqConfig.COMMERCE_PAYMENT_CANCELLED_QUEUE)
    public void onPaymentCancelling(PaymentCompletedEvent event) {
        User user = userRepo.findUserByUuid(event.userUuid())
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", event.userUuid()));

        String subject = "Status Update: Your BookReaders Order";
        String body = "We inform you that your recent order (Order ID: " + event.orderUuid() + ") has been canceled." +
                "\n If you have any questions or concerns, please contact our support team.";
        try {
            emailService.sendReceipt(user.getEmail(), subject, body);
        } catch (MessagingException ex) {
            log.warn("Failed to send receipt email: {}", ex.getMessage());
        }
    }
}
