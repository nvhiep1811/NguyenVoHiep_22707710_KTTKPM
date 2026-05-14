package com.kttkpm.bookingservice.dto;

import com.kttkpm.bookingservice.domain.Booking;
import com.kttkpm.bookingservice.domain.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record BookingResponse(
        String id,
        String userId,
        String movieId,
        String showTimeId,
        String movieTitle,
        Integer seats,
        BigDecimal totalPrice,
        BookingStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getMovieId(),
                booking.getShowTimeId(),
                booking.getMovieTitle(),
                booking.getSeats(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getUpdatedAt());
    }
}
