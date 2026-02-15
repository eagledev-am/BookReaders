package com.eagledev.bookreaders.services.notification;

import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.dtos.notification.EmailNotification;
import com.eagledev.bookreaders.dtos.notification.NotificationEvent;
import com.eagledev.bookreaders.services.email.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    private EmailService emailService;

    @RabbitListener(queues = RabbitMqConfig.NOTIFICATION_QUEUE)
    public void onMessage(NotificationEvent notificationEvent){
        // excute
    }

    @RabbitListener(queues = RabbitMqConfig.NOTIFICATION_QUEUE)
    public void onMessage(NotificationEvent notificationEvent, EmailNotification emailNotification) throws MessagingException {
        emailService.sendEmail(emailNotification);
    }
}
