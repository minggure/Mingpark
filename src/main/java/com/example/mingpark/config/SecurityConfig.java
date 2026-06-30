package com.example.mingpark.config;

import com.example.mingpark.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
//    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 현재 정적 HTML + fetch 구조에서는 CSRF 토큰을 따로 안 보내므로 우선 비활성화
                .csrf(csrf -> csrf.disable())

                // JWT가 아니라 세션 방식이므로 필요할 때 세션 생성
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 페이지, 이미지, 업로드 파일
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/signup.html",
                                "/register.html",
                                "/images/**",
                                "/uploads/**",
                                "/error",
                                "/members/kakao/login",
                                "/members/kakao/callback"
                        ).permitAll()

                        // 회원가입, 로그인 API
                        .requestMatchers(HttpMethod.POST, "/members/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/members/login").permitAll()

                        // 공연 조회 API는 누구나 가능
                        .requestMatchers(HttpMethod.GET, "/api/concerts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/concerts/*").permitAll()

                        // 관리자만 가능
                        .requestMatchers(HttpMethod.POST, "/api/concerts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/concert-images").hasRole("ADMIN")

                        // 로그인한 사용자만 가능
                        .requestMatchers("/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/concerts/*/seats").authenticated()

                        // 나머지는 일단 허용 또는 인증 필요 중 선택
                        // 안전하게 가려면 authenticated() 추천
                        .anyRequest().permitAll()
                )

                // 기본 로그인 폼은 안 씀. 지금 프로젝트는 /members/login JSON API 사용
                .formLogin(form -> form.disable())

                // 기본 HTTP Basic 인증도 안 씀
                .httpBasic(basic -> basic.disable())

                // 로그아웃 URL 설정
                .logout(logout -> logout
                        .logoutUrl("/members/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"status\":\"success\"}");
                        })
                )

                // 로그인 실패/권한 부족 응답
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"status\":\"unauthorized\",\"message\":\"로그인이 필요합니다.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"status\":\"forbidden\",\"message\":\"권한이 없습니다.\"}");
                        })
                )

                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}