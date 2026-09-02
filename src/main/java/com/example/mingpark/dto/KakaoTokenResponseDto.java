package com.example.mingpark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoTokenResponseDto {
    private final String accessToken;
    private final String tokenType;
    private final String refreshToken;
    private final Integer expiresIn;
}
