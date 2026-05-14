package com.kttkpm.bookingservice.dto;

import java.math.BigDecimal;

public record PaymentCompletedEvent(String paymentId, String bookingId, String userId, BigDecimal amount) {
}
