package com.kttkpm.paymentservice.dto;

public record BookingFailedEvent(
        String bookingId,
        String userId,
        String movieId,
        String showTimeId,
        Integer seats,
        String reason) {
}
