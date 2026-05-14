package com.kttkpm.bookingservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kttkpm.bookingservice.dto.BookingFailedEvent;
import com.kttkpm.bookingservice.dto.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(BookingEventConsumer.class);

    private final BookingService bookingService;
    private final ObjectMapper objectMapper;

    public BookingEventConsumer(BookingService bookingService, ObjectMapper objectMapper) {
        this.bookingService = bookingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentCompleted(String payload) throws Exception {
        PaymentCompletedEvent event = objectMapper.treeToValue(readPayload(payload), PaymentCompletedEvent.class);
        bookingService.confirmBooking(event.bookingId());
        log.info("Booking confirmed from PAYMENT_COMPLETED bookingId={}", event.bookingId());
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookingFailed(String payload) throws Exception {
        BookingFailedEvent event = objectMapper.treeToValue(readPayload(payload), BookingFailedEvent.class);
        bookingService.failBooking(event.bookingId());
        log.info("Booking failed from BOOKING_FAILED bookingId={}, reason={}", event.bookingId(), event.reason());
    }

    private JsonNode readPayload(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        return root.hasNonNull("payload") ? root.get("payload") : root;
    }
}
