package com.fooddelivery.order.dto;

import java.math.BigDecimal;

public record CreateOrderResponse(
        String orderId,
        String status,
        String paymentStatus,
        BigDecimal totalAmount,
        String message
) {
}

