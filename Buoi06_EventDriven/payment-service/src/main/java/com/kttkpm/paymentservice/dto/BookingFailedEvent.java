package com.kttkpm.paymentservice.dto;

public record BookingFailedEvent(String bookingId, String userId, String reason) {
}
