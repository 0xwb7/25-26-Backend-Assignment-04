package org.example.simplejwtexample.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotNull(message = "비밀번호 입력은 필수입니다.")
    private String password;
}
