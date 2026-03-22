package nvhiep.userservice.controller;

import jakarta.validation.Valid;
import nvhiep.userservice.dto.CreateUserProfileRequest;
import nvhiep.userservice.dto.UpdateUserProfileRequest;
import nvhiep.userservice.dto.UserProfileResponse;
import nvhiep.userservice.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<UserProfileResponse> findAll() {
        return userProfileService.findAll();
    }

    @GetMapping("/{userId}")
    public UserProfileResponse findByUserId(@PathVariable Long userId) {
        return userProfileService.findByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileResponse create(@Valid @RequestBody CreateUserProfileRequest request) {
        return userProfileService.create(request);
    }

    @PutMapping("/{userId}")
    public UserProfileResponse update(@PathVariable Long userId,
                                      @Valid @RequestBody UpdateUserProfileRequest request) {
        return userProfileService.updateByUserId(userId, request);
    }
}
