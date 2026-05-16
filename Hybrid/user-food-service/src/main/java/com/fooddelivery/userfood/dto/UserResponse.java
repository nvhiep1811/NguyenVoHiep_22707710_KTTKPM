package com.fooddelivery.userfood.dto;

import java.time.Instant;

public record UserResponse(
        String id,
        String fullName,
        String email,
        String phone,
        String role,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}

