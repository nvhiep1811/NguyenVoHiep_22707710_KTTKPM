package com.fooddelivery.order.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderCreatedEvent(
        String eventId,
        String eventType,
        String orderId,
        String userId,
        BigDecimal amount,
        String paymentMethod,
        List<OrderCreatedItem> items,
        Instant createdAt
) {
}

