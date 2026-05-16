package com.fooddelivery.payment.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentFailedEvent(
        String eventId,
        String eventType,
        String orderId,
        String userId,
        BigDecimal amount,
        String reason,
        Instant failedAt
) {
}

