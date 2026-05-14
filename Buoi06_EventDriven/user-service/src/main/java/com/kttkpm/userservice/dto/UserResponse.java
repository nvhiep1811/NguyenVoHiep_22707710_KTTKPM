package com.kttkpm.userservice.dto;

import com.kttkpm.userservice.domain.Role;
import java.time.Instant;

public record UserResponse(
        String id,
        String username,
        String email,
        Role role,
        Instant createdAt
) {
}
