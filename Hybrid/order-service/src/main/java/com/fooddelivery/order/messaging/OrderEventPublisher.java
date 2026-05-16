package com.fooddelivery.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fooddelivery.order.config.RabbitMQConfig;
import com.fooddelivery.order.event.OrderCreatedEvent;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                    event
            );
            log.info("Published ORDER_CREATED event for order {}", event.orderId());
        } catch (Exception ex) {
            log.error("Failed to publish ORDER_CREATED event for order {}", event.orderId(), ex);
            throw new IllegalStateException("Failed to publish ORDER_CREATED event", ex);
        }
    }
}

