package com.fooddelivery.order.event;

import java.math.BigDecimal;

public record OrderCreatedItem(
        String foodId,
        String foodName,
        BigDecimal price,
        int quantity
) {
}

