package org.example.simplejwtexample.controller;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.dto.user.LoginRequest;
import org.example.simplejwtexample.dto.user.SignUpRequest;
import org.example.simplejwtexample.dto.user.TokenDto;
import org.example.simplejwtexample.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signUp(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(signUpRequest));
    }

    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
