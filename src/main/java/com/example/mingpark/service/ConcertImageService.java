package com.example.mingpark.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * 공연 포스터 파일 검증과 로컬 저장을 담당한다.
 * 이미지 파일 자체는 uploads 폴더에 저장하고, DB에는 반환된 접근 경로만 저장한다.
 */
@Service
public class ConcertImageService {

    // 허용할 MIME 타입과 저장할 파일 확장자를 함께 관리한다.
    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp"
    );

    /**
     * 붙여넣은 이미지를 서버 uploads 폴더에 저장하고 화면에서 사용할 경로를 반환한다.
     */
    public String upload(MultipartFile image) {
        String extension = IMAGE_EXTENSIONS.get(image.getContentType());

        // 빈 파일 또는 허용 목록에 없는 파일은 저장하지 않는다.
        if (image.isEmpty() || extension == null) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }

        Path uploadDirectory = Path.of("uploads").toAbsolutePath().normalize();
        // 원본 파일명 충돌과 특수문자 문제를 피하기 위해 UUID 파일명을 사용한다.
        String fileName = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(uploadDirectory);
            image.transferTo(uploadDirectory.resolve(fileName));
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
        }

        // Concert.image 필드와 HTML img 태그에서 사용할 공개 접근 경로를 반환한다.
        return "/uploads/" + fileName;
    }
}
