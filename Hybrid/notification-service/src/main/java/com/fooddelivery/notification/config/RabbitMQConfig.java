package com.fooddelivery.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "food.delivery.exchange";

    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    public static final String PAYMENT_SUCCESS_NOTIFICATION_QUEUE = "payment.success.notification.queue";
    public static final String PAYMENT_FAILED_NOTIFICATION_QUEUE = "payment.failed.notification.queue";

    @Bean
    public TopicExchange foodDeliveryExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentSuccessNotificationQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue paymentFailedNotificationQueue() {
        return QueueBuilder.durable(PAYMENT_FAILED_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding paymentSuccessNotificationBinding(Queue paymentSuccessNotificationQueue, TopicExchange foodDeliveryExchange) {
        return BindingBuilder.bind(paymentSuccessNotificationQueue).to(foodDeliveryExchange).with(PAYMENT_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding paymentFailedNotificationBinding(Queue paymentFailedNotificationQueue, TopicExchange foodDeliveryExchange) {
        return BindingBuilder.bind(paymentFailedNotificationQueue).to(foodDeliveryExchange).with(PAYMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("*");
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
