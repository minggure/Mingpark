package com.example.mingpark.controller;


import com.example.mingpark.domain.Member;
import com.example.mingpark.dto.LoginRequestDto;
import com.example.mingpark.dto.MemberSignupRequestDto;

import com.example.mingpark.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // HTML 회원가입 폼 데이터를 검증한 뒤 회원가입 결과를 쿼리 파라미터로 전달
    @PostMapping("/members/signup")
    public String signup(
            @Valid @ModelAttribute MemberSignupRequestDto request,
            BindingResult bindingResult
    ) {
        // DTO 검증에 실패하면 회원가입 페이지에서 입력 오류 알림을 표시
        if (bindingResult.hasErrors()) {
            return "redirect:/signup.html?error=validation";
        }

        try {
            memberService.signup(request);
            return "redirect:/signup.html?success=true";
        } catch (IllegalArgumentException e) {
            // 이미 존재하는 로그인 아이디인 경우 중복 알림을 표시
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

        // 로그인 실패 시 ➡ 다시 static/login.html로
        if (loginMember == null) {
            return "redirect:/login.html?error=failed";
        }

        // 로그인 성공 시 ➡ 세션 저장
        jakarta.servlet.http.HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        return "redirect:/login.html?success=true";
    }
}
