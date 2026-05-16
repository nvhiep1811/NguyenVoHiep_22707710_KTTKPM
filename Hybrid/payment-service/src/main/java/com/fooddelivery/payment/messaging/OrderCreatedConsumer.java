package com.fooddelivery.payment.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fooddelivery.payment.config.RabbitMQConfig;
import com.fooddelivery.payment.event.OrderCreatedEvent;
import com.fooddelivery.payment.service.PaymentService;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private final PaymentService paymentService;

    public OrderCreatedConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_PAYMENT_QUEUE)
    public void consumeOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Received ORDER_CREATED event for order #{}", event.orderId());
            paymentService.processOrderCreated(event);
        } catch (Exception ex) {
            log.error("Error while consuming ORDER_CREATED event for order {}", event.orderId(), ex);
        }
    }
}

