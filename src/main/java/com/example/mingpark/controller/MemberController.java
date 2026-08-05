package com.example.mingpark.controller;

import com.example.mingpark.domain.Member;
import com.example.mingpark.dto.KakaoTokenResponseDto;
import com.example.mingpark.dto.KakaoUserResponseDto;
import com.example.mingpark.dto.LoginRequestDto;
import com.example.mingpark.dto.MemberSignupRequestDto;
import com.example.mingpark.service.KakaoService;
import com.example.mingpark.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 회원 컨트롤러
 * API:
 * POST /members/signup - 신규 회원 가입 처리
 * POST /members/login  - 스프링 시큐리티 및 세션 기반 로그인 인증 처리
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final KakaoService kakaoService;
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

    @GetMapping("/members/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.ok(Map.of("status", "anonymous"));
        }

        return ResponseEntity.ok(Map.of(
                "status", "authenticated",
                "memberName", userDetails.getName(),
                "role", userDetails.getRole().name()
        ));
    }

    @GetMapping("/members/kakao/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoService.getKakaoLoginUrl());
    }

    @GetMapping("/members/kakao/callback")
    public void kakaoCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, name = "error_description") String errorDescription,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        if (error != null || code == null || code.isBlank()) {
            String message = errorDescription != null && !errorDescription.isBlank()
                    ? errorDescription
                    : "카카오 인증 코드가 없습니다. 카카오 로그인 버튼으로 다시 시도해 주세요.";
            redirectKakaoError(response, message);
            return;
        }
        // 카카오 인증 코드로 토큰 발급
        try {
            KakaoTokenResponseDto token = kakaoService.getToken(code);
            KakaoUserResponseDto kakaoUser = kakaoService.getUserInfo(token.getAccessToken());

            String nickname = null;
            String email = null;
            // 프로필 정보가 없거나, 동의하지않았을 경우에 서버 터짐 방지
            if (kakaoUser.getKakaoAccount() != null) {
                email = kakaoUser.getKakaoAccount().getEmail();

                if (kakaoUser.getKakaoAccount().getProfile() != null) {
                    nickname = kakaoUser.getKakaoAccount().getProfile().getNickname();
                }
            }
            // 이게 있어야 카카오 로그인시 회원가입 중복X
            Member member = memberService.findOrCreateKakaoMember(
                    kakaoUser.getId(), // 카카오 회원번호
                    nickname,
                    email
            );

            CustomUserDetails userDetails = new CustomUserDetails(member);

            // 스프링 시큐리티 인증 객체 만들기
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken( // 로그인정보를 표현하는 객체
                            userDetails,
                            null, // 카카오인증 끝났으므로 비번 필요X
                            userDetails.getAuthorities() // 권한 목록
                    );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            // 세션에 로그인 상태 저장,true는 세션없으면 새로 생성
            HttpSession session = request.getSession(true);
            session.setAttribute(  // 로그인 정보 세션에 저장하는 코드(세션 로그인 유지 역할)
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );

            response.sendRedirect("/index.html");
        } catch (RuntimeException e) {
            log.warn("Kakao login failed.", e);
            redirectKakaoError(response, "카카오 로그인 처리 중 오류가 발생했습니다. 설정을 확인한 뒤 다시 시도해 주세요.");
        }
    }
    // 카카오 로그인 실패 시 로그인 페이지로 다시 보내는 공통 메서드입
    private void redirectKakaoError(HttpServletResponse response, String message) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect("/login.html?kakaoError=" + encodedMessage);
    }
}
