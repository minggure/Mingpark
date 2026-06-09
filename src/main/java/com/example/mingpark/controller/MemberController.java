package com.example.mingpark.controller;

import com.example.mingpark.dto.MemberSignupRequest;
import com.example.mingpark.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/signup")
    public String signup(MemberSignupRequest request) {
        memberService.signup(request);
        return "redirect:/signup.html";
    }
}