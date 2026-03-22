package nvhiep.monolithicapp.dto;

import jakarta.validation.Valid;

public record UpdateUserProfileRequest(
        @Valid UserProfilePayload profile
) {
}
