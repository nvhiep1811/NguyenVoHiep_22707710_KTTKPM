package com.kttkpm.paymentservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kttkpm.paymentservice.config.PaymentProcessingProperties;
import com.kttkpm.paymentservice.domain.Payment;
import com.kttkpm.paymentservice.domain.PaymentStatus;
import com.kttkpm.paymentservice.dto.BookingCreatedEvent;
import com.kttkpm.paymentservice.repository.PaymentRepository;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PaymentProcessorService {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessorService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final PaymentProcessingProperties processingProperties;
    private final ObjectMapper objectMapper;

    public PaymentProcessorService(
            PaymentRepository paymentRepository,
            PaymentEventPublisher paymentEventPublisher,
            PaymentProcessingProperties processingProperties,
            ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.processingProperties = processingProperties;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingCreated(String payload) throws Exception {
        BookingCreatedEvent event = readBookingCreatedEvent(payload);
        process(event);
    }

    public Payment process(BookingCreatedEvent event) {
        validate(event);
        return paymentRepository.findByBookingId(event.bookingId())
                .orElseGet(() -> createAndProcessPayment(event));
    }

    private Payment createAndProcessPayment(BookingCreatedEvent event) {
        try {
            Payment payment = paymentRepository.save(new Payment(event.bookingId(), event.userId(), event.totalPrice()));
            boolean success = ThreadLocalRandom.current().nextDouble() < processingProperties.getSuccessRate();

            if (success) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setProcessedAt(Instant.now());
                Payment savedPayment = paymentRepository.save(payment);
                paymentEventPublisher.publishPaymentCompleted(savedPayment);
                log.info("Payment completed for bookingId={}", savedPayment.getBookingId());
                return savedPayment;
            }

            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailReason(randomFailureReason());
            payment.setProcessedAt(Instant.now());
            Payment savedPayment = paymentRepository.save(payment);
            paymentEventPublisher.publishBookingFailed(savedPayment, event);
            log.info("Payment failed for bookingId={}, reason={}", savedPayment.getBookingId(), savedPayment.getFailReason());
            return savedPayment;
        } catch (DuplicateKeyException ex) {
            return paymentRepository.findByBookingId(event.bookingId()).orElseThrow(() -> ex);
        }
    }

    private BookingCreatedEvent readBookingCreatedEvent(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        JsonNode eventPayload = root.hasNonNull("payload") ? root.get("payload") : root;
        return objectMapper.treeToValue(eventPayload, BookingCreatedEvent.class);
    }

    private void validate(BookingCreatedEvent event) {
        if (event == null || !StringUtils.hasText(event.bookingId()) || !StringUtils.hasText(event.userId())
                || event.totalPrice() == null || event.totalPrice().signum() <= 0) {
            throw new IllegalArgumentException("Invalid BOOKING_CREATED event");
        }
    }

    private String randomFailureReason() {
        List<String> reasons = processingProperties.getFailureReasons();
        if (reasons == null || reasons.isEmpty()) {
            return "Payment rejected";
        }
        int index = ThreadLocalRandom.current().nextInt(reasons.size());
        return reasons.get(index);
    }
}
