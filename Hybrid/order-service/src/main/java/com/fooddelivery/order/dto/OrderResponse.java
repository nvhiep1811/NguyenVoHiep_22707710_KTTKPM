package com.fooddelivery.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fooddelivery.order.model.OrderItem;

public record OrderResponse(
        String id,
        String userId,
        List<OrderItem> items,
        BigDecimal totalAmount,
        String paymentMethod,
        String status,
        String paymentStatus,
        Instant createdAt,
        Instant updatedAt
) {
}

