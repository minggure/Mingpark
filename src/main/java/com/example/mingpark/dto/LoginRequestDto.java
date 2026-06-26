package com.example.mingpark.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
/**
 * 로그인 인증 요청 DTO.
 */
@Getter
@RequiredArgsConstructor
public class LoginRequestDto {
    private final String loginId;
    private final String password;
}