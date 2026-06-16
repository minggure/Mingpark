package com.example.mingpark.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter @Setter
@RequiredArgsConstructor
public class LoginRequestDto {
    private final String loginId;
    private final String password;
}