package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.domain.Role;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.user.LoginRequest;
import org.example.simplejwtexample.dto.user.SignUpRequest;
import org.example.simplejwtexample.dto.user.TokenDto;
import org.example.simplejwtexample.jwt.TokenProvider;
import org.example.simplejwtexample.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public TokenDto signUp(SignUpRequest signUpRequest) {
        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(user -> {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        });

        User saveUser = userRepository.save(User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getUserName())
                .role(Role.ROLE_USER)
                .build());

        String token = tokenProvider.createToken(saveUser.getId(), saveUser.getRole().name());
        return TokenDto.builder()
                .accessToken(token)
                .build();
    }

    public TokenDto login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = tokenProvider.createToken(user.getId(), user.getRole().name());
        return TokenDto.builder()
                .accessToken(token)
                .build();
    }
}
