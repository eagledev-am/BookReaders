package com.eagledev.bookreaders.services.notification;

import com.eagledev.bookreaders.config.RabbitMqConfig;
import com.eagledev.bookreaders.dtos.notification.NotificationEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private final AmqpTemplate template;

    public NotificationPublisher(@Qualifier("amqpTemplate") AmqpTemplate template) {
        this.template = template;
    }

    public void sendNotification(NotificationEvent notification){
        this.template.convertAndSend(RabbitMqConfig.NOTIFICATION_EXCHANGE,RabbitMqConfig.NOTIFICATION_ROUTING_KEY,notification);
    }

}
