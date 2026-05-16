package com.fooddelivery.payment.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fooddelivery.payment.config.RabbitMQConfig;
import com.fooddelivery.payment.event.PaymentFailedEvent;
import com.fooddelivery.payment.event.PaymentSuccessEvent;

@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY,
                    event
            );
            log.info("Published PAYMENT_SUCCESS event for order {}", event.orderId());
        } catch (Exception ex) {
            log.error("Failed to publish PAYMENT_SUCCESS event for order {}", event.orderId(), ex);
            throw new IllegalStateException("Failed to publish PAYMENT_SUCCESS event", ex);
        }
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.PAYMENT_FAILED_ROUTING_KEY,
                    event
            );
            log.info("Published PAYMENT_FAILED event for order {}", event.orderId());
        } catch (Exception ex) {
            log.error("Failed to publish PAYMENT_FAILED event for order {}", event.orderId(), ex);
            throw new IllegalStateException("Failed to publish PAYMENT_FAILED event", ex);
        }
    }
}

