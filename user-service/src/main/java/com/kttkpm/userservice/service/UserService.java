package com.kttkpm.userservice.service;

import com.kttkpm.userservice.domain.User;
import com.kttkpm.userservice.domain.UserRole;
import com.kttkpm.userservice.domain.UserStatus;
import com.kttkpm.userservice.dto.request.UpdateUserRequest;
import com.kttkpm.userservice.dto.request.UpdateUserRoleRequest;
import com.kttkpm.userservice.dto.request.UpdateUserStatusRequest;
import com.kttkpm.userservice.dto.response.UserResponse;
import com.kttkpm.userservice.dto.response.UserValidationResponse;
import com.kttkpm.userservice.exception.ResourceNotFoundException;
import com.kttkpm.userservice.mapper.UserMapper;
import com.kttkpm.userservice.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getUsers(UserRole role, UserStatus status, String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);

        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .filter(user -> role == null || user.getRole() == role)
                .filter(user -> status == null || user.getStatus() == status)
                .filter(user -> !StringUtils.hasText(normalizedKeyword)
                        || user.getFullName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                        || user.getEmail().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse getUserById(String id) {
        return userMapper.toResponse(findUserEntityById(id));
    }

    public UserResponse getCurrentUser(String currentUserId) {
        return getUserById(currentUserId);
    }

    public UserResponse updateProfile(String id, UpdateUserRequest request) {
        User user = findUserEntityById(id);
        user.setFullName(normalizeFullName(request.fullName()));
        user.setUpdatedAt(Instant.now());
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse updateRole(String id, UpdateUserRoleRequest request) {
        User user = findUserEntityById(id);
        user.setRole(request.role());
        user.setUpdatedAt(Instant.now());
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse updateStatus(String id, UpdateUserStatusRequest request) {
        User user = findUserEntityById(id);
        user.setStatus(request.status());
        user.setUpdatedAt(Instant.now());
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserValidationResponse validateUser(String id) {
        return userMapper.toValidationResponse(findUserEntityById(id));
    }

    private User findUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private String normalizeFullName(String fullName) {
        return fullName.trim().replaceAll("\\s+", " ");
    }

    private String normalizeKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return keyword.trim().toLowerCase(Locale.ROOT);
    }
}
