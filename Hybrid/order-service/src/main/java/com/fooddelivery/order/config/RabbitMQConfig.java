package com.fooddelivery.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "food.delivery.exchange";

    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    public static final String ORDER_CREATED_PAYMENT_QUEUE = "order.created.payment.queue";
    public static final String PAYMENT_SUCCESS_ORDER_QUEUE = "payment.success.order.queue";
    public static final String PAYMENT_FAILED_ORDER_QUEUE = "payment.failed.order.queue";

    @Bean
    public TopicExchange foodDeliveryExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue orderCreatedPaymentQueue() {
        return QueueBuilder.durable(ORDER_CREATED_PAYMENT_QUEUE).build();
    }

    @Bean
    public Queue paymentSuccessOrderQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_ORDER_QUEUE).build();
    }

    @Bean
    public Queue paymentFailedOrderQueue() {
        return QueueBuilder.durable(PAYMENT_FAILED_ORDER_QUEUE).build();
    }

    @Bean
    public Binding orderCreatedPaymentBinding(Queue orderCreatedPaymentQueue, TopicExchange foodDeliveryExchange) {
        return BindingBuilder.bind(orderCreatedPaymentQueue).to(foodDeliveryExchange).with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentSuccessOrderBinding(Queue paymentSuccessOrderQueue, TopicExchange foodDeliveryExchange) {
        return BindingBuilder.bind(paymentSuccessOrderQueue).to(foodDeliveryExchange).with(PAYMENT_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding paymentFailedOrderBinding(Queue paymentFailedOrderQueue, TopicExchange foodDeliveryExchange) {
        return BindingBuilder.bind(paymentFailedOrderQueue).to(foodDeliveryExchange).with(PAYMENT_FAILED_ROUTING_KEY);
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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
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
