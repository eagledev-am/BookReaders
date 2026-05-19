package com.eagledev.bookreaders.services.listeners;

import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.dtos.notification.EmailNotification;
import com.eagledev.bookreaders.services.email.EmailNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationListener {
    private final EmailNotificationService emailNotificationService;

    @RabbitListener(queues = RabbitMqConfig.USER_EMAIL_QUEUE)
    public void onMessage(EmailNotification emailNotification) throws MessagingException {
        emailNotificationService.sendEmail(emailNotification);
    }
}
