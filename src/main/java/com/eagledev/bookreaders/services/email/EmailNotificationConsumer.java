package com.eagledev.bookreaders.services.email;

import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.dtos.notification.EmailNotification;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMqConfig.EMAIL_QUEUE)
    public void onMessage(EmailNotification emailNotification) throws MessagingException {
        emailService.sendEmail(emailNotification);
    }
}
