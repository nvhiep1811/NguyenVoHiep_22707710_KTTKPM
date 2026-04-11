package com.kttkpm.userservice.dto.response;

import com.kttkpm.userservice.domain.UserRole;
import com.kttkpm.userservice.domain.UserStatus;

public record UserValidationResponse(
        String id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status,
        boolean active
) {
}
