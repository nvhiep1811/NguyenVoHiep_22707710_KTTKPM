package nvhiep.userservice.dto;

import java.time.LocalDate;

public record UserProfileResponse(
        Long id,
        Long userId,
        String fullName,
        String phone,
        String address,
        LocalDate dateOfBirth,
        String avatarUrl
) {
}
