package com.kttkpm.bookingservice.dto;

import java.math.BigDecimal;

public record BookingCreatedEvent(
        String bookingId,
        String userId,
        String movieId,
        String showTimeId,
        Integer seats,
        BigDecimal totalPrice,
        String movieTitle) {
}
