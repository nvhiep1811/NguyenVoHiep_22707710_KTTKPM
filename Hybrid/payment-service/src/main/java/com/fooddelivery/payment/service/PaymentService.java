package com.fooddelivery.payment.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fooddelivery.payment.event.OrderCreatedEvent;
import com.fooddelivery.payment.event.PaymentFailedEvent;
import com.fooddelivery.payment.event.PaymentSuccessEvent;
import com.fooddelivery.payment.messaging.PaymentEventPublisher;
import com.fooddelivery.payment.model.PaymentDocument;
import com.fooddelivery.payment.model.PaymentMethod;
import com.fooddelivery.payment.model.PaymentStatus;
import com.fooddelivery.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final DateTimeFormatter TRANSACTION_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentService(PaymentRepository paymentRepository, PaymentEventPublisher paymentEventPublisher) {
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    public void processOrderCreated(OrderCreatedEvent event) {
        PaymentDocument payment = new PaymentDocument();
        payment.setOrderId(event.orderId());
        payment.setUserId(event.userId());
        payment.setAmount(event.amount());
        payment.setMethod(PaymentMethod.valueOf(event.paymentMethod()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(Instant.now());

        PaymentDocument saved = paymentRepository.save(payment);

        boolean success = ThreadLocalRandom.current().nextInt(100) < 80;
        if (success) {
            handleSuccess(event, saved);
        } else {
            handleFailed(event, saved);
        }
    }

    private void handleSuccess(OrderCreatedEvent event, PaymentDocument payment) {
        Instant paidAt = Instant.now();
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionCode(generateTransactionCode());
        payment.setPaidAt(paidAt);
        paymentRepository.save(payment);

        PaymentSuccessEvent successEvent = new PaymentSuccessEvent(
                UUID.randomUUID().toString(),
                "PAYMENT_SUCCESS",
                event.orderId(),
                event.userId(),
                event.amount(),
                payment.getTransactionCode(),
                paidAt
        );
        paymentEventPublisher.publishPaymentSuccess(successEvent);
        log.info("Payment SUCCESS for order {}", event.orderId());
    }

    private void handleFailed(OrderCreatedEvent event, PaymentDocument payment) {
        Instant failedAt = Instant.now();
        String reason = "Random payment failed";
        payment.setStatus(PaymentStatus.FAILED);
        payment.setReason(reason);
        paymentRepository.save(payment);

        PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                "PAYMENT_FAILED",
                event.orderId(),
                event.userId(),
                event.amount(),
                reason,
                failedAt
        );
        paymentEventPublisher.publishPaymentFailed(failedEvent);
        log.info("Payment FAILED for order {}", event.orderId());
    }

    private String generateTransactionCode() {
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "PAY-" + TRANSACTION_TIME_FORMAT.format(Instant.now()) + "-" + random;
    }
}

