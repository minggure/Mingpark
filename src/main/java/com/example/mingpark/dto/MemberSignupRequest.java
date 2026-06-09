package com.example.mingpark.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberSignupRequest {
    private String name;
    private String loginId;
    private String password;
    private String email;
}