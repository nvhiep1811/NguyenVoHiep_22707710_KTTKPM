package nvhiep.monolithicapp.dto;

import jakarta.validation.constraints.NotBlank;

public record MonolithLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
