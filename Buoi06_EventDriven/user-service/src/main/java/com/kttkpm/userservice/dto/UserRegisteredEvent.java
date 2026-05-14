package com.kttkpm.userservice.dto;

public record UserRegisteredEvent(
        String userId,
        String email,
        String username
) {
}
