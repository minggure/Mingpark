package com.example.mingpark.controller;

import com.example.mingpark.dto.MyPageMainResponseDto;
import com.example.mingpark.security.CustomUserDetails;
import com.example.mingpark.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 컨트롤러
 * API:
 * GET /api/users/me/points - 마이페이지 프로필 및 포인트 정보 조회
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 마이페이지 메인 화면에 필요한 프로필 및 잔여 포인트 데이터를 조회한다.
     *
     * @param userDetails 인증된 사용자 정보 객체
     * @return 200 OK 및 마이페이지 요약 DTO 반환, 인증 누락 시 401 오류 반환
     */
    @GetMapping("/users/me/points")
    public ResponseEntity<?> getMyPageSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        MyPageMainResponseDto summary = myPageService.getMyPageSummary(userDetails.getMemberId());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/users/me/reservations")
    public ResponseEntity<?> getMyReservations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(myPageService.getMyReservations(userDetails.getMemberId()));
    }

    @GetMapping("/users/me/reservations/{reservationId}")
    public ResponseEntity<?> getMyReservationDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reservationId
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(myPageService.getMyReservationDetail(userDetails.getMemberId(), reservationId));
    }
}