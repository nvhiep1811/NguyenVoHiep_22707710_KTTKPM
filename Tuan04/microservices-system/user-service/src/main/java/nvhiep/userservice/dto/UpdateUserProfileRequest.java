package nvhiep.userservice.dto;

import java.time.LocalDate;

public record UpdateUserProfileRequest(
        String fullName,
        String phone,
        String address,
        LocalDate dateOfBirth,
        String avatarUrl
) {
}
