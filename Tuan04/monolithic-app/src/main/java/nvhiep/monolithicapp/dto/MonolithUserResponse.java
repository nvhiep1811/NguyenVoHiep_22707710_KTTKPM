package nvhiep.monolithicapp.dto;

import java.time.LocalDate;
import java.util.Set;

public record MonolithUserResponse(
        Long id,
        String username,
        String email,
        String status,
        Set<String> roles,
        String fullName,
        String phone,
        String address,
        LocalDate dateOfBirth,
        String avatarUrl
) {
}
