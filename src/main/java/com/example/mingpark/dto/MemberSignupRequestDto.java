package com.example.mingpark.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class MemberSignupRequestDto {

    // 빈 값과 DB 컬럼 길이를 함께 검증
    @NotBlank
    @Size(max = 20)
    private String name;

    // 로그인 아이디는 4자 이상
    @NotBlank
    @Size(min = 4, max = 50)
    private String loginId;

    // 너무 짧은 비밀번호 입력을 방지
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    // 빈 값, 이메일 형식, DB 컬럼 길이를 함께 검증
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
}
