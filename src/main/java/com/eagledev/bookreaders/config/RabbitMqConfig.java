package com.eagledev.bookreaders.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMqConfig {
    public static final String EMAIL_EXCHANGE="EMAIL_EXCHANGE";
    public static final String EMAIL_QUEUE="EMAIL_QUEUE";
    public static final String EMAIL_ROUTING_KEY="EMAIL_KEY";

    public static final String NOTIFICATION_EXCHANGE="NOTIFICATION_EXCHANGE";
    public static final String NOTIFICATION_QUEUE="NOTIFICATION_QUEUE";
    public static final String NOTIFICATION_ROUTING_KEY="NOTIFICATION_KEY";

    @Bean
    public Queue emailQueue(){
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue notificationQueue(){
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public TopicExchange emailTopicExchange(){
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationTopicExchange(){
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding emailBinding(){
        return BindingBuilder
                .bind(emailQueue())
                .to(emailTopicExchange())
                .with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding(){
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationTopicExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
