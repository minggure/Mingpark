package com.example.mingpark.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class MemberSignupRequestDto {

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Size(min = 4, max = 50)
    private String loginId;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
}
