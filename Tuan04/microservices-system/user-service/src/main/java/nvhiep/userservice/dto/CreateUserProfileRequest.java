package nvhiep.userservice.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateUserProfileRequest(
        @NotNull Long userId,
        String fullName,
        String phone,
        String address,
        LocalDate dateOfBirth,
        String avatarUrl
) {
}
