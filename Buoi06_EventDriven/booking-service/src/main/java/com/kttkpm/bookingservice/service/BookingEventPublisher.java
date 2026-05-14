package com.kttkpm.bookingservice.service;

import com.kttkpm.bookingservice.domain.Booking;
import com.kttkpm.bookingservice.dto.BookingCreatedEvent;
import com.kttkpm.bookingservice.dto.DomainEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String bookingCreatedTopic;

    public BookingEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.booking-created}") String bookingCreatedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.bookingCreatedTopic = bookingCreatedTopic;
    }

    public void publishBookingCreated(Booking booking) {
        BookingCreatedEvent payload = new BookingCreatedEvent(
                booking.getId(),
                booking.getUserId(),
                booking.getMovieId(),
                booking.getShowTimeId(),
                booking.getSeats(),
                booking.getTotalPrice(),
                booking.getMovieTitle());
        DomainEvent<BookingCreatedEvent> event = DomainEvent.published(
                "BOOKING_CREATED",
                "booking-service",
                payload);
        try {
            new com.kttkpm.bookingservice.config.ResilientKafkaPublisher(kafkaTemplate)
                    .publishWithRetry(bookingCreatedTopic, booking.getId(), event);
        } catch (Exception e) {
            // log and continue
        }
    }
}
