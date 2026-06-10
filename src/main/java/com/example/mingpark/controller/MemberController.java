package com.example.mingpark.controller;


import com.example.mingpark.dto.MemberSignupRequestDto;

import com.example.mingpark.domain.Member;
import com.example.mingpark.dto.LoginRequestDto;
import com.example.mingpark.dto.MemberSignupRequestDto;

import com.example.mingpark.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/signup")
    public String signup(
            @Valid @ModelAttribute MemberSignupRequestDto request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/signup.html?error=validation";
        }

        try {
            memberService.signup(request);
            return "redirect:/signup.html?success=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/signup.html?error=duplicate";
        }
    }


    @GetMapping("/members/login")
    public String loginForm() {
        return "redirect:/login.html";
    }
    @PostMapping("/members/login")
    public String login(@ModelAttribute LoginRequestDto loginDto,
                        jakarta.servlet.http.HttpServletRequest request) {

        Member loginMember = memberService.login(loginDto);

        // 로그인 실패 시 ➡️ 다시 static/login.html로
        if (loginMember == null) {
            System.out.println("🚨 로그인 실패! 아이디나 비번이 틀렸어!");
            return "redirect:/login.html"; // 👈 여기도 정적 파일 주소로 정확히 매핑!
        }

        // 로그인 성공 시 ➡️ 세션 저장
        jakarta.servlet.http.HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        System.out.println("🚀 로그인 성공!!! 환영합니다, " + loginMember.getName() + "님!");

        return "redirect:/login.html";
    }
}
