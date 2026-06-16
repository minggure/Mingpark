package com.example.mingpark.controller;

import com.example.mingpark.domain.Member;
import com.example.mingpark.dto.LoginRequestDto;
import com.example.mingpark.dto.MemberSignupRequestDto;
import com.example.mingpark.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 API
    @PostMapping("/members/signup")
    public ResponseEntity<?> signup(
            // @RequestBody : 프론트에서 보낸 JSON, MemberSignupRequestDto 객체로 변환
            // @Valid : Dto에 작성된 @NotBlank, @Size, @Email 조건 검사
            @Valid @RequestBody MemberSignupRequestDto request,
            BindingResult bindingResult
            // BindingResult : 검증 실패 내용을 확인하는 객체
            // 실패시 Service 호출 안함, 오류 응답 반환 (밑에 if문)

    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("status", "validation_error"));
        }

        try {
            // 성공시 Service 호출
            memberService.signup(request);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "duplicate"));
        }
    }

    @PostMapping("/members/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpServletRequest request) {

        Member loginMember = memberService.login(loginDto);

        // 로그인 실패 시
        if (loginMember == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "failed"));
        }

        // 로그인 성공 시 세션 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "memberName", loginMember.getName()
        ));
    }
}