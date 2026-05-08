package com.kttkpm.notificationservice.dto;

public record BookingFailedEvent(String bookingId, String userId, String reason) {
}
