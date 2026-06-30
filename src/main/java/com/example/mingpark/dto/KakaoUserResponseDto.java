package com.example.mingpark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserResponseDto {
    private final Long id;
    private final KakaoAccount kakaoAccount;


    @Getter
    @AllArgsConstructor
    public static class KakaoAccount {
        private final String email;
        private final Profile profile;
    }


    @Getter
    @AllArgsConstructor
    public static class Profile {
        private final String nickname;
    }
}
