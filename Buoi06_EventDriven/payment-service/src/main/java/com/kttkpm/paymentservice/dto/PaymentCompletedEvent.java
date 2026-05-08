package com.kttkpm.paymentservice.dto;

import java.math.BigDecimal;

public record PaymentCompletedEvent(String paymentId, String bookingId, String userId, BigDecimal amount) {
}
