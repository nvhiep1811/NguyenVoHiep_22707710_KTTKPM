package com.fooddelivery.notification.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fooddelivery.notification.config.RabbitMQConfig;
import com.fooddelivery.notification.event.PaymentFailedEvent;
import com.fooddelivery.notification.event.PaymentSuccessEvent;
import com.fooddelivery.notification.service.NotificationService;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final NotificationService notificationService;

    public PaymentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_NOTIFICATION_QUEUE)
    public void consumePaymentSuccess(PaymentSuccessEvent event) {
        try {
            log.info("Received PAYMENT_SUCCESS event for notification, order {}", event.orderId());
            notificationService.createPaymentSuccessNotification(event);
        } catch (Exception ex) {
            log.error("Error while consuming PAYMENT_SUCCESS event for notification, order {}", event.orderId(), ex);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_NOTIFICATION_QUEUE)
    public void consumePaymentFailed(PaymentFailedEvent event) {
        try {
            log.info("Received PAYMENT_FAILED event for notification, order {}", event.orderId());
            notificationService.createPaymentFailedNotification(event);
        } catch (Exception ex) {
            log.error("Error while consuming PAYMENT_FAILED event for notification, order {}", event.orderId(), ex);
        }
    }
}

