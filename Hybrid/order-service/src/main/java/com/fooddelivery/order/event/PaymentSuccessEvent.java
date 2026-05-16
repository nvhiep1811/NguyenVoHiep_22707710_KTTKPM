package com.fooddelivery.order.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentSuccessEvent(
        String eventId,
        String eventType,
        String orderId,
        String userId,
        BigDecimal amount,
        String transactionCode,
        Instant paidAt
) {
}

