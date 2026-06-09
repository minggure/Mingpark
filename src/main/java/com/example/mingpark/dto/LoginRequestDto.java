package com.example.mingpark.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestDto {
    private String loginId;
    private String password;
}