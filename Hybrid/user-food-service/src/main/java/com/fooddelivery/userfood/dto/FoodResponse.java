package com.fooddelivery.userfood.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record FoodResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String category,
        String restaurantName,
        boolean available,
        Instant createdAt,
        Instant updatedAt
) {
}

