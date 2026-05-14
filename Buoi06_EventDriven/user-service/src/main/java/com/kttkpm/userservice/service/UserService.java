package com.kttkpm.userservice.service;

import com.kttkpm.userservice.domain.User;
import com.kttkpm.userservice.dto.UserLoginRequest;
import com.kttkpm.userservice.dto.UserRegisterRequest;
import com.kttkpm.userservice.dto.UserRegisteredEvent;
import com.kttkpm.userservice.dto.UserResponse;
import com.kttkpm.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.user-registered}")
    private String userRegisteredTopic;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UserResponse register(UserRegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(request.password()),
                request.role()
        );

        User savedUser = userRepository.save(user);
        log.info("User registered: {}", savedUser.getUsername());

        // Publish event
        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername()
        );
        
        // publish with retry and DLQ fallback
        try {
            var publisher = new com.kttkpm.userservice.config.ResilientKafkaPublisher(kafkaTemplate);
            publisher.publishWithRetry(userRegisteredTopic, savedUser.getId(), event);
            log.info("Published USER_REGISTERED event for user: {}", savedUser.getUsername());
        } catch (Exception e) {
            log.error("Event publish failed unexpectedly: {}", e.toString());
        }

        return mapToResponse(savedUser);
    }

    public UserResponse login(UserLoginRequest request) {
        String identity = request.username().trim();

        User user = userRepository.findByUsername(identity)
                .or(() -> userRepository.findByEmail(identity.toLowerCase(Locale.ROOT)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        log.info("User logged in: {}", user.getUsername());
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
