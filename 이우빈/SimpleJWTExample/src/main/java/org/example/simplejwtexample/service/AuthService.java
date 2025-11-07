package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.domain.RefreshToken;
import org.example.simplejwtexample.domain.Role;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.user.LoginRequest;
import org.example.simplejwtexample.dto.user.SignUpRequest;
import org.example.simplejwtexample.dto.user.TokenDto;
import org.example.simplejwtexample.exception.BadRequestException;
import org.example.simplejwtexample.exception.ErrorMessage;
import org.example.simplejwtexample.jwt.TokenProvider;
import org.example.simplejwtexample.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(user -> {
            throw new BadRequestException(ErrorMessage.ALREADY_EXIST_EMAIL);
        });

        userRepository.save(User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getUserName())
                .role(Role.ROLE_USER)
                .build());
    }

    @Transactional
    public TokenDto login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.WRONG_EMAIL_INPUT));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_PASSWORD_INPUT);
        }

        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), user.getRole().name());

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        rt -> rt.updateRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(user.getId())
                                        .token(refreshToken)
                                        .build()
                        )
                );

        return tokenBuilder(accessToken, refreshToken);
    }

    @Transactional
    public TokenDto refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_TOKEN));

        Long userId = tokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        if (!storedRefreshToken.getUserId().equals(user.getId())) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        String newAccessToken = tokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = tokenProvider.createRefreshToken(user.getId(), user.getRole().name());

        storedRefreshToken.updateRefreshToken(newRefreshToken);

        return tokenBuilder(newAccessToken, newRefreshToken);
    }

    private TokenDto tokenBuilder(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
