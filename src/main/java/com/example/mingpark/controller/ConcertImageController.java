package com.example.mingpark.controller;

import com.example.mingpark.service.ConcertImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 공연 등록 화면에서 클립보드로 붙여넣은 포스터 이미지 업로드 요청을 처리한다.
 * 공연 정보 등록 API와 분리되어 있으며, 업로드 성공 후 반환한 imageUrl을 공연 등록 요청에 사용한다.
 */
@RestController
@RequiredArgsConstructor
public class ConcertImageController {

    private final ConcertImageService concertImageService;

    // multipart/form-data의 image 파일을 저장하고 브라우저에서 접근 가능한 URL을 반환한다.
    @PostMapping("/api/concert-images")
    public ResponseEntity<?> uploadConcertImage(@RequestParam MultipartFile image) {
        try {
            String imageUrl = concertImageService.upload(image);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
