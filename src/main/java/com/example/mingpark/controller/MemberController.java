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
import com.example.mingpark.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Map;

/**
 * 회원 컨트롤러
 * API:
 * POST /members/signup - 신규 회원 가입 처리
 * POST /members/login  - 스프링 시큐리티 및 세션 기반 로그인 인증 처리
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    /**
     * [POST] 입력받은 회원 정보를 검증하여 시스템에 등록 처리.
     *
     * @param request 가입할 아이디, 비밀번호, 이름, 이메일 등이 담긴 DTO
     * @param bindingResult 데이터 입력 유효성 검증 실패 내역을 저장하는 객체
     * @return 200 OK 및 가입 성공 응답 반환, 검증 실패 시 400 Bad Request 및 에러 코드 반환, 아이디 중복 시 400 및 중복 코드 반환
     */
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


//    @PostMapping("/members/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpServletRequest request) {
//
//        Member loginMember = memberService.login(loginDto);
//
//        // 로그인 실패 시
//        if (loginMember == null) {
//            return ResponseEntity.badRequest().body(Map.of("status", "failed"));
//        }

//        // 로그인 성공 시 세션 저장
//        HttpSession session = request.getSession();
//        session.setAttribute("loginMember", loginMember);
//
//        return ResponseEntity.ok(Map.of(
//                "status", "success",
//                "memberName", loginMember.getName(),
//                "role", loginMember.getRole().name()
//                // 로그인 성공 응답에 role 추가
//        ));
//    }

    /**
     * 입력받은 아이디와 비밀번호를 AuthenticationManager를 통해 검증하고,
     * 인증 성공 시 시큐리티 컨텍스트 및 서블릿 세션에 저장 처리.
     *
     * @param loginDto 클라이언트가 보낸 로그인 요청 정보
     * @param request 세션 획득과 로그인 상태 저장하는 HTTP 서블릿 요청 객체
     * @return 로그인 성공시 200 OK 및 사용자 프로필 정보(이름, 권한 등), 인증 실패 시 400 Bad Request 및 실패 코드 반환
     */
    @PostMapping("/members/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getLoginId(),
                            loginDto.getPassword()
                    )
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "memberName", userDetails.getName(),
                    "role", userDetails.getRole().name()
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "failed"));
        }
    }
    // 로그아웃 API
    // 브라우저 localStorage, 서버 세션 제거
    // 시큐리티 컨피그에 이미있음
//    @PostMapping("/members/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.invalidate();
//        }
//
//        return ResponseEntity.ok(Map.of("status", "success"));
//    }
}
