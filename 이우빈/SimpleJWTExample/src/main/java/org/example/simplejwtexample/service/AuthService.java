package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.domain.Role;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.user.LoginRequest;
import org.example.simplejwtexample.dto.user.SignUpRequest;
import org.example.simplejwtexample.dto.user.TokenDto;
import org.example.simplejwtexample.exception.BadRequestException;
import org.example.simplejwtexample.exception.ErrorMessage;
import org.example.simplejwtexample.jwt.TokenProvider;
import org.example.simplejwtexample.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(user -> {
            throw new BadRequestException(ErrorMessage.ALREADY_EXIST_EMAIL);
        });

        User saveUser = userRepository.save(User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getUserName())
                .role(Role.ROLE_USER)
                .build());

        String token = tokenProvider.createToken(saveUser.getId(), saveUser.getRole().name());
    }

    @Transactional
    public TokenDto login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.WRONG_EMAIL_INPUT));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_PASSWORD_INPUT);
        }

        String token = tokenProvider.createToken(user.getId(), user.getRole().name());
        return TokenDto.builder()
                .accessToken(token)
                .build();
    }
}
