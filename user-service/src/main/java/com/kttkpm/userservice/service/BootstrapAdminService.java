package com.kttkpm.userservice.service;

import com.kttkpm.userservice.config.AdminBootstrapProperties;
import com.kttkpm.userservice.domain.User;
import com.kttkpm.userservice.domain.UserRole;
import com.kttkpm.userservice.domain.UserStatus;
import com.kttkpm.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Locale;

@Component
public class BootstrapAdminService implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapAdminService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminBootstrapProperties adminBootstrapProperties;

    public BootstrapAdminService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AdminBootstrapProperties adminBootstrapProperties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminBootstrapProperties = adminBootstrapProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminBootstrapProperties.getEmail())
                || !StringUtils.hasText(adminBootstrapProperties.getPassword())) {
            LOGGER.info("Skipping admin bootstrap because ADMIN_EMAIL or ADMIN_PASSWORD is missing");
            return;
        }

        String email = adminBootstrapProperties.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            LOGGER.info("Admin bootstrap skipped because {} already exists", email);
            return;
        }

        Instant now = Instant.now();

        User admin = new User();
        admin.setFullName(normalizeFullName(adminBootstrapProperties.getFullName()));
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(adminBootstrapProperties.getPassword()));
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        userRepository.save(admin);
        LOGGER.info("Bootstrapped default admin account: {}", email);
    }

    private String normalizeFullName(String fullName) {
        if (!StringUtils.hasText(fullName)) {
            return "System Admin";
        }
        return fullName.trim().replaceAll("\\s+", " ");
    }
}
