package nvhiep.monolithicapp.dto;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String passwordHash,
        String status,
        Set<String> roles,
        @Valid UserProfilePayload profile
) {
}
