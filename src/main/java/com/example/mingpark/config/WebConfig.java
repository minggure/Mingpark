package com.example.mingpark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * 실행 중 업로드된 공연 포스터를 /uploads 경로로 제공한다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 외부 저장소 업로드 경로를 웹 URL 컨텍스트 주소와 매핑 처리.
     *
     * @param registry 자원 핸들러 레지스트리
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로젝트 실행 위치의 uploads 폴더를 절대 경로로 변환한다.
        Path uploadDirectory = Path.of("uploads").toAbsolutePath().normalize();

        // DB에 저장된 /uploads/파일명 주소로 실제 업로드 이미지를 조회할 수 있게 연결한다.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDirectory.toUri().toString());
    }

}
