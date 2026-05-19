package com.eagledev.bookreaders.services.user;

import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.dtos.notification.EmailNotification;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationPublisher {
    private final AmqpTemplate template;

    public EmailNotificationPublisher(@Qualifier("amqpTemplate") AmqpTemplate template) {
        this.template = template;
    }

    public void sendEmailNotification(EmailNotification emailNotification){
        this.template.convertAndSend(
                RabbitMqConfig.USER_EMAIL_EXCHNAGE,
                RabbitMqConfig.USER_EMAIL_ROUTING_KEY,
                emailNotification
        );
    }
}
