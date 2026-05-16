package com.fooddelivery.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fooddelivery.order.config.RabbitMQConfig;
import com.fooddelivery.order.event.PaymentFailedEvent;
import com.fooddelivery.order.event.PaymentSuccessEvent;
import com.fooddelivery.order.service.OrderService;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final OrderService orderService;

    public PaymentEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_ORDER_QUEUE)
    public void consumePaymentSuccess(PaymentSuccessEvent event) {
        try {
            log.info("Received PAYMENT_SUCCESS event for order {}", event.orderId());
            orderService.markPaymentSuccess(event);
        } catch (Exception ex) {
            log.error("Error while consuming PAYMENT_SUCCESS event for order {}", event.orderId(), ex);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_ORDER_QUEUE)
    public void consumePaymentFailed(PaymentFailedEvent event) {
        try {
            log.info("Received PAYMENT_FAILED event for order {}", event.orderId());
            orderService.markPaymentFailed(event);
        } catch (Exception ex) {
            log.error("Error while consuming PAYMENT_FAILED event for order {}", event.orderId(), ex);
        }
    }
}

