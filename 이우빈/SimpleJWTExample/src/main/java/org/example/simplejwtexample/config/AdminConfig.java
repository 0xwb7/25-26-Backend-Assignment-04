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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner setAdmin() {
        return args -> {
            String adminEmail = "admin@admin.com";

            userRepository.findByEmail(adminEmail)
                    .orElseGet(() -> userRepository.save(
                            User.builder()
                                    .name("Admin")
                                    .email(adminEmail)
                                    .password(passwordEncoder.encode("admin123"))
                                    .role(Role.ROLE_ADMIN)
                                    .build()
                    ));
        };
    }
}
