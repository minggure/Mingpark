package com.example.mingpark.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequestDto {
    private final String loginId;
    private final String password;
}