package nvhiep.monolithicapp.dto;

import java.util.Set;

public record MonolithLoginResponse(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        String username,
        Set<String> roles
) {
}
