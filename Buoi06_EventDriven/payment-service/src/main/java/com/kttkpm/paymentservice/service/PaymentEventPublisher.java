package com.kttkpm.paymentservice.service;

import com.kttkpm.paymentservice.domain.Payment;
import com.kttkpm.paymentservice.dto.BookingCreatedEvent;
import com.kttkpm.paymentservice.dto.BookingFailedEvent;
import com.kttkpm.paymentservice.dto.DomainEvent;
import com.kttkpm.paymentservice.dto.PaymentCompletedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentCompletedTopic;
    private final String bookingFailedTopic;

    public PaymentEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.payment-completed}") String paymentCompletedTopic,
            @Value("${app.kafka.topics.booking-failed}") String bookingFailedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentCompletedTopic = paymentCompletedTopic;
        this.bookingFailedTopic = bookingFailedTopic;
    }

    public void publishPaymentCompleted(Payment payment) {
        PaymentCompletedEvent payload = new PaymentCompletedEvent(
                payment.getId(),
                payment.getBookingId(),
                payment.getUserId(),
                payment.getAmount());
        DomainEvent<PaymentCompletedEvent> event = DomainEvent.published(
                "PAYMENT_COMPLETED",
                "payment-service",
                payload);
                try {
                        new com.kttkpm.paymentservice.config.ResilientKafkaPublisher(kafkaTemplate)
                                        .publishWithRetry(paymentCompletedTopic, payment.getBookingId(), event);
                } catch (Exception e) {
                        // ignore
                }
    }

    public void publishBookingFailed(Payment payment, BookingCreatedEvent bookingCreatedEvent) {
        BookingFailedEvent payload = new BookingFailedEvent(
                payment.getBookingId(),
                payment.getUserId(),
                bookingCreatedEvent.movieId(),
                bookingCreatedEvent.showTimeId(),
                bookingCreatedEvent.seats(),
                payment.getFailReason());
        DomainEvent<BookingFailedEvent> event = DomainEvent.published(
                "BOOKING_FAILED",
                "payment-service",
                payload);
                try {
                        new com.kttkpm.paymentservice.config.ResilientKafkaPublisher(kafkaTemplate)
                                        .publishWithRetry(bookingFailedTopic, payment.getBookingId(), event);
                } catch (Exception e) {
                        // ignore
                }
    }
}
