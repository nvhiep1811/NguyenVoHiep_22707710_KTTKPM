package nvhiep.monolithicapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import nvhiep.monolithicapp.dto.CreateUserRequest;
import nvhiep.monolithicapp.dto.MonolithLoginRequest;
import nvhiep.monolithicapp.dto.MonolithLoginResponse;
import nvhiep.monolithicapp.dto.MonolithRegisterRequest;
import nvhiep.monolithicapp.dto.MonolithUserResponse;
import nvhiep.monolithicapp.dto.UserProfilePayload;
import nvhiep.monolithicapp.entity.Role;
import nvhiep.monolithicapp.entity.User;
import nvhiep.monolithicapp.entity.UserProfile;
import nvhiep.monolithicapp.repository.RoleRepository;
import nvhiep.monolithicapp.repository.UserProfileRepository;
import nvhiep.monolithicapp.repository.UserRepository;
import nvhiep.monolithicapp.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class MonolithUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public MonolithUserService(UserRepository userRepository,
                               RoleRepository roleRepository,
                               UserProfileRepository userProfileRepository,
                               PasswordEncoder passwordEncoder,
                               JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public List<MonolithUserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MonolithUserResponse findUserById(Long id) {
        User user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    @Transactional
    public MonolithUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Set<String> roleNames = request.roles() == null || request.roles().isEmpty()
                ? Set.of("USER")
                : request.roles();

        List<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more roles are invalid");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
            .passwordHash(passwordEncoder.encode(request.passwordHash()))
                .status(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status())
                .createdAt(LocalDateTime.now())
                .roles(Set.copyOf(roles))
                .build();

        User savedUser = userRepository.save(user);

        if (request.profile() != null) {
            userProfileRepository.save(buildProfile(savedUser, request.profile()));
        }

        User reloaded = userRepository.findWithRolesById(savedUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot reload user"));
        return toResponse(reloaded);
    }

    @Transactional
    public MonolithUserResponse register(MonolithRegisterRequest request) {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                request.username(),
                request.email(),
                request.password(),
                "ACTIVE",
                Set.of("USER"),
                request.profile()
        );
        return createUser(createUserRequest);
    }

    @Transactional(readOnly = true)
    public MonolithLoginResponse login(MonolithLoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        boolean passwordValid;
        try {
            passwordValid = passwordEncoder.matches(request.password(), user.getPasswordHash());
        } catch (IllegalArgumentException ex) {
            passwordValid = request.password().equals(user.getPasswordHash());
        }

        if (!passwordValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        String token = jwtService.generateToken(
                user.getUsername(),
                java.util.Map.of("roles", roles, "userId", user.getId())
        );

        return new MonolithLoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getUsername(),
                roles
        );
    }

    @Transactional
    public MonolithUserResponse updateProfile(Long userId, UserProfilePayload payload) {
        User user = userRepository.findWithRolesById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> UserProfile.builder()
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .build());

        profile.setFullName(payload.fullName());
        profile.setPhone(payload.phone());
        profile.setAddress(payload.address());
        profile.setDateOfBirth(payload.dateOfBirth());
        profile.setAvatarUrl(payload.avatarUrl());
        profile.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(profile);
        return toResponse(user);
    }

    private UserProfile buildProfile(User user, UserProfilePayload payload) {
        return UserProfile.builder()
                .user(user)
                .fullName(payload.fullName())
                .phone(payload.phone())
                .address(payload.address())
                .dateOfBirth(payload.dateOfBirth())
                .avatarUrl(payload.avatarUrl())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private MonolithUserResponse toResponse(User user) {
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        return new MonolithUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                profile != null ? profile.getFullName() : null,
                profile != null ? profile.getPhone() : null,
                profile != null ? profile.getAddress() : null,
                profile != null ? profile.getDateOfBirth() : null,
                profile != null ? profile.getAvatarUrl() : null
        );
    }
}
