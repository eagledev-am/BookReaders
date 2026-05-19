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
    public static final String USER_EMAIL_EXCHNAGE="USER_EMAIL_EXCHNAGE";
    public static final String USER_EMAIL_QUEUE="USER_EMAIL_QUEUE";
    public static final String USER_EMAIL_ROUTING_KEY="EMAIL_KEY";

    public static final String COMMERCE_PAYMENT_EXCHANGE = "COMMERCE_PAYMENT_EXCHANGE";
    public static final String COMMERCE_PAYMENT_ROUTING_KEY = "COMMERCE_PAYMENT_COMPLETED";
    public static final String COMMERCE_PAYMENT_FULFILLMENT_QUEUE = "COMMERCE_PAYMENT_FULFILLMENT_QUEUE";
    public static final String COMMERCE_PAYMENT_RECEIPT_QUEUE = "COMMERCE_PAYMENT_RECEIPT_QUEUE";

    @Bean
    public Queue emailQueue(){
        return new Queue(USER_EMAIL_QUEUE, true);
    }

    @Bean
    public TopicExchange emailTopicExchange(){
        return new TopicExchange(USER_EMAIL_EXCHNAGE);
    }

    @Bean
    public Binding emailBinding(){
        return BindingBuilder
                .bind(emailQueue())
                .to(emailTopicExchange())
                .with(USER_EMAIL_ROUTING_KEY);
    }

    @Bean
    public Queue commercePaymentFulfillmentQueue() {
        return new Queue(COMMERCE_PAYMENT_FULFILLMENT_QUEUE, true);
    }

    @Bean
    public Queue commercePaymentReceiptQueue() {
        return new Queue(COMMERCE_PAYMENT_RECEIPT_QUEUE, true);
    }

    @Bean
    public TopicExchange commercePaymentExchange() {
        return new TopicExchange(COMMERCE_PAYMENT_EXCHANGE);
    }

    @Bean
    public Binding commercePaymentFulfillmentBinding() {
        return BindingBuilder
                .bind(commercePaymentFulfillmentQueue())
                .to(commercePaymentExchange())
                .with(COMMERCE_PAYMENT_ROUTING_KEY);
    }

    @Bean
    public Binding commercePaymentReceiptBinding() {
        return BindingBuilder
                .bind(commercePaymentReceiptQueue())
                .to(commercePaymentExchange())
                .with(COMMERCE_PAYMENT_ROUTING_KEY);
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
