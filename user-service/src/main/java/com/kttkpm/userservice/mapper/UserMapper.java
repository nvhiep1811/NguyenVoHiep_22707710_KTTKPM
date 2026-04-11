package com.kttkpm.userservice.mapper;

import com.kttkpm.userservice.domain.User;
import com.kttkpm.userservice.domain.UserStatus;
import com.kttkpm.userservice.dto.response.UserResponse;
import com.kttkpm.userservice.dto.response.UserValidationResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserValidationResponse toValidationResponse(User user) {
        return new UserValidationResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getStatus() == UserStatus.ACTIVE
        );
    }
}
