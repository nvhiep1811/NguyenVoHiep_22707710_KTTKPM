package com.kttkpm.userservice.dto.request;

import com.kttkpm.userservice.domain.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull(message = "Role is required")
        UserRole role
) {
}
