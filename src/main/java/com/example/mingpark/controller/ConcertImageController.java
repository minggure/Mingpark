package com.example.mingpark.controller;

import com.example.mingpark.service.ConcertImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ConcertImageController {

    private final ConcertImageService concertImageService;

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