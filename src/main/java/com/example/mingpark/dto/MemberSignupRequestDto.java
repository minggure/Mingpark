package com.example.mingpark.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberSignupRequestDto {
// 회원가입 요청값을 전달, 입력 형식을 검증
    @NotBlank
    @Size(max = 20)
    private final String name;

    @NotBlank
    @Size(min = 4, max = 50)
    private final String loginId;

    @NotBlank
    @Size(min = 8, max = 100)
    private final String password;

    @NotBlank
    @Email
    @Size(max = 100)
    private final String email;
}
