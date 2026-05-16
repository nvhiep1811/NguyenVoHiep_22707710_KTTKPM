package com.fooddelivery.userfood.service;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fooddelivery.userfood.dto.AuthResponse;
import com.fooddelivery.userfood.dto.LoginRequest;
import com.fooddelivery.userfood.dto.RegisterRequest;
import com.fooddelivery.userfood.exception.ApiException;
import com.fooddelivery.userfood.model.UserDocument;
import com.fooddelivery.userfood.model.UserRole;
import com.fooddelivery.userfood.model.UserStatus;
import com.fooddelivery.userfood.repository.UserRepository;
import com.fooddelivery.userfood.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Instant now = Instant.now();
        UserDocument user = new UserDocument();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserDocument saved = userRepository.save(user);
        return toAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        UserDocument user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "User account is locked");
        }

        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(UserDocument user) {
        return new AuthResponse(jwtUtil.generateToken(user), user.getId(), user.getEmail(), user.getRole().name());
    }
}

