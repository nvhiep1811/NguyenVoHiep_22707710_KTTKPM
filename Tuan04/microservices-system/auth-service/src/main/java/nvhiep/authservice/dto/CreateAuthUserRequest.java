package nvhiep.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CreateAuthUserRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String passwordHash,
        String status,
        Set<String> roles
) {
}
