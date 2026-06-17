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

    /**
     * 입력받은 아이디와 비밀번호를 검증하여 일치하면 서블릿 세션에 회원 정보를 저장하고,
     * 성공 여부와 회원의 이름을 응답으로 반환
     * @param loginDto 클라이언트가 보낸 로그인 요청 정보
     * @param request 세션 획득과 로그인 상태 저장하는 HTTP 서블릿 요청 객체
     * @return 로그인 성공시 이름과 200(OK)응답 / 로그인 실패시 400(BADREQUSET)응답
     */
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