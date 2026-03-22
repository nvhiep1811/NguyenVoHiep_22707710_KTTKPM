package nvhiep.monolithicapp.dto;

import java.time.LocalDate;

public record UserProfilePayload(
        String fullName,
        String phone,
        String address,
        LocalDate dateOfBirth,
        String avatarUrl
) {
}
