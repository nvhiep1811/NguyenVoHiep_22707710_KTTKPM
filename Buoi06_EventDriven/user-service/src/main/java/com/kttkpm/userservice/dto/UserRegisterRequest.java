package com.kttkpm.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.kttkpm.userservice.domain.Role;

public record UserRegisterRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
        @NotBlank(message = "Password is required") String password,
        Role role
) {
    public UserRegisterRequest {
        if (role == null) {
            role = Role.USER;
        }
    }
}
