package org.example.simplejwtexample.config;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.domain.Role;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminConfig {

    private static final String ADMIN_NAME = "admin";
    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner setAdmin() {
        return args -> {
            userRepository.findByEmail(ADMIN_EMAIL)
                    .orElseGet(() -> userRepository.save(
                            User.builder()
                                    .name(ADMIN_NAME)
                                    .email(ADMIN_EMAIL)
                                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                                    .role(Role.ROLE_ADMIN)
                                    .build()
                    ));
        };
    }
}
