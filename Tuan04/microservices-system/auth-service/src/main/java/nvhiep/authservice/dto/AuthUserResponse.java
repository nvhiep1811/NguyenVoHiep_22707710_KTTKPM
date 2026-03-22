package nvhiep.authservice.dto;

import java.util.Set;

public record AuthUserResponse(
        Long id,
        String username,
        String email,
        String status,
        Set<String> roles
) {
}
