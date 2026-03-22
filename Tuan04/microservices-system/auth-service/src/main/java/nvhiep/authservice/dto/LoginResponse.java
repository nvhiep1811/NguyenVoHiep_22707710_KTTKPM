package nvhiep.authservice.dto;

import java.util.Set;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        String username,
        Set<String> roles
) {
}
