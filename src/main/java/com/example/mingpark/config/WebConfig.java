package com.example.mingpark.config;

import com.example.mingpark.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * 실행 중 업로드된 공연 포스터를 /uploads 경로로 제공한다.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .order(1) // 인터셉터가 실행될 순서 (1번으로 실행)

                // 🔒 [감시할 주소 설정] 로그인이 필요한 주소 및 API 패턴을 정확히 등록!
                .addPathPatterns(
                        "/reservations/**",      // 내 예약 보기 화면 등
                        "/api/reservations/**",  // 예매하기 POST API 🌟 (추가)
                        "/api/concerts/**/seats" // 좌석 배치도 조회 API 🌟 (추가: 로그인 검증용)
                )

                // 🔓 [패스할 주소 설정] 로그인 안 해도 무조건 통과할 주소들!
                .excludePathPatterns(
                        "/",                    // 메인 페이지
                        "/members/signup",      // 회원가입 API
                        "/members/login",       // 로그인 API
                        "/css/**", "/js/**", "/images/**", "/error" // 정적 리소스 파일들
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로젝트 실행 위치의 uploads 폴더를 절대 경로로 변환한다.
        Path uploadDirectory = Path.of("uploads").toAbsolutePath().normalize();

        // DB에 저장된 /uploads/파일명 주소로 실제 업로드 이미지를 조회할 수 있게 연결한다.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDirectory.toUri().toString());
    }

}
