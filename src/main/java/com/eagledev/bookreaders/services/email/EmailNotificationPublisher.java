package com.eagledev.bookreaders.services.email;

import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.dtos.notification.EmailNotification;
import lombok.RequiredArgsConstructor;
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
                RabbitMqConfig.EMAIL_EXCHANGE,
                RabbitMqConfig.EMAIL_ROUTING_KEY,
                emailNotification
        );
    }
}
