package com.example.japanweb.config;

import com.example.japanweb.config.properties.AdminSeedProperties;
import com.example.japanweb.entity.User;
import com.example.japanweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeedInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedProperties adminSeedProperties;

    @Override
    public void run(ApplicationArguments args) {
        long adminCount = userRepository.countByRole(User.Role.ADMIN);
        if (adminCount > 0) {
            return;
        }

        if (!StringUtils.hasText(adminSeedProperties.getUsername())
                || !StringUtils.hasText(adminSeedProperties.getEmail())
                || !StringUtils.hasText(adminSeedProperties.getPassword())) {
            log.warn(
                    "No admin account found and admin seed credentials are missing. " +
                    "Set ADMIN_USERNAME, ADMIN_EMAIL, and ADMIN_PASSWORD."
            );
            return;
        }

        if (userRepository.existsByUsername(adminSeedProperties.getUsername())) {
            log.warn("Cannot seed admin user because username '{}' already exists.", adminSeedProperties.getUsername());
            return;
        }

        if (userRepository.existsByEmail(adminSeedProperties.getEmail())) {
            log.warn("Cannot seed admin user because email '{}' already exists.", adminSeedProperties.getEmail());
            return;
        }

        User admin = User.builder()
                .username(adminSeedProperties.getUsername())
                .email(adminSeedProperties.getEmail())
                .passwordHash(passwordEncoder.encode(adminSeedProperties.getPassword()))
                .role(User.Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Seeded initial admin user '{}' from environment.", admin.getUsername());
    }
}
