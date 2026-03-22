package nvhiep.authservice.service;

import nvhiep.authservice.dto.AuthUserResponse;
import nvhiep.authservice.dto.CreateAuthUserRequest;
import nvhiep.authservice.dto.LoginRequest;
import nvhiep.authservice.dto.LoginResponse;
import nvhiep.authservice.dto.RegisterRequest;
import nvhiep.authservice.entity.Role;
import nvhiep.authservice.entity.UserAuth;
import nvhiep.authservice.repository.RoleRepository;
import nvhiep.authservice.repository.UserAuthRepository;
import nvhiep.authservice.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthUserService {

    private final UserAuthRepository userAuthRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthUserService(UserAuthRepository userAuthRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userAuthRepository = userAuthRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public List<AuthUserResponse> findAll() {
        return userAuthRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AuthUserResponse findById(Long id) {
        UserAuth userAuth = userAuthRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auth user not found"));
        return toResponse(userAuth);
    }

    @Transactional
    public AuthUserResponse create(CreateAuthUserRequest request) {
        if (userAuthRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userAuthRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Set<String> roleNames = request.roles() == null || request.roles().isEmpty()
                ? Set.of("USER")
                : request.roles();

        List<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more roles are invalid");
        }

        UserAuth userAuth = UserAuth.builder()
                .username(request.username())
                .email(request.email())
            .passwordHash(passwordEncoder.encode(request.passwordHash()))
                .status(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status())
                .createdAt(LocalDateTime.now())
                .roles(Set.copyOf(roles))
                .build();

        UserAuth saved = userAuthRepository.save(userAuth);
        return toResponse(saved);
    }

    @Transactional
    public AuthUserResponse register(RegisterRequest request) {
        CreateAuthUserRequest createRequest = new CreateAuthUserRequest(
                request.username(),
                request.email(),
                request.password(),
                "ACTIVE",
                Set.of("USER")
        );
        return create(createRequest);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UserAuth userAuth = userAuthRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        boolean passwordValid;
        try {
            passwordValid = passwordEncoder.matches(request.password(), userAuth.getPasswordHash());
        } catch (IllegalArgumentException ex) {
            passwordValid = request.password().equals(userAuth.getPasswordHash());
        }

        if (!passwordValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Set<String> roles = userAuth.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        String token = jwtService.generateToken(
                userAuth.getUsername(),
                java.util.Map.of("roles", roles, "userId", userAuth.getId())
        );

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                userAuth.getUsername(),
                roles
        );
    }

    @Transactional
    public AuthUserResponse assignRoles(Long userId, Set<String> roleNames) {
        UserAuth userAuth = userAuthRepository.findWithRolesById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auth user not found"));

        List<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more roles are invalid");
        }

        userAuth.setRoles(Set.copyOf(roles));
        UserAuth saved = userAuthRepository.save(userAuth);
        return toResponse(saved);
    }

    private AuthUserResponse toResponse(UserAuth userAuth) {
        return new AuthUserResponse(
                userAuth.getId(),
                userAuth.getUsername(),
                userAuth.getEmail(),
                userAuth.getStatus(),
                userAuth.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }
}
