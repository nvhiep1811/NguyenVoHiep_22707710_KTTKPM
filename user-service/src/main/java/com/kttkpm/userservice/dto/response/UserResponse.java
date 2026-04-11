package com.kttkpm.userservice.dto.response;

import com.kttkpm.userservice.domain.UserRole;
import com.kttkpm.userservice.domain.UserStatus;

import java.time.Instant;

public record UserResponse(
        String id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
