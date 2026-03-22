package nvhiep.userservice.service;

import nvhiep.userservice.dto.CreateUserProfileRequest;
import nvhiep.userservice.dto.UpdateUserProfileRequest;
import nvhiep.userservice.dto.UserProfileResponse;
import nvhiep.userservice.entity.UserProfile;
import nvhiep.userservice.repository.UserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> findAll() {
        return userProfileRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse findByUserId(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return toResponse(userProfile);
    }

    @Transactional
    public UserProfileResponse create(CreateUserProfileRequest request) {
        if (userProfileRepository.existsByUserId(request.userId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile for this user already exists");
        }

        UserProfile userProfile = UserProfile.builder()
                .userId(request.userId())
                .fullName(request.fullName())
                .phone(request.phone())
                .address(request.address())
                .dateOfBirth(request.dateOfBirth())
                .avatarUrl(request.avatarUrl())
                .createdAt(LocalDateTime.now())
                .build();

        UserProfile saved = userProfileRepository.save(userProfile);
        return toResponse(saved);
    }

    @Transactional
    public UserProfileResponse updateByUserId(Long userId, UpdateUserProfileRequest request) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        userProfile.setFullName(request.fullName());
        userProfile.setPhone(request.phone());
        userProfile.setAddress(request.address());
        userProfile.setDateOfBirth(request.dateOfBirth());
        userProfile.setAvatarUrl(request.avatarUrl());
        userProfile.setUpdatedAt(LocalDateTime.now());

        UserProfile updated = userProfileRepository.save(userProfile);
        return toResponse(updated);
    }

    private UserProfileResponse toResponse(UserProfile userProfile) {
        return new UserProfileResponse(
                userProfile.getId(),
                userProfile.getUserId(),
                userProfile.getFullName(),
                userProfile.getPhone(),
                userProfile.getAddress(),
                userProfile.getDateOfBirth(),
                userProfile.getAvatarUrl()
        );
    }
}
