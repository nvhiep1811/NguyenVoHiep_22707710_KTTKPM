package com.fooddelivery.userfood.dto;

public record AuthResponse(
        String token,
        String userId,
        String email,
        String role
) {
}

