package com.example.mingpark.controller;

import com.example.mingpark.dto.WaitingStatusResponseDto;
import com.example.mingpark.security.CustomUserDetails;
import com.example.mingpark.service.WaitingService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts/{concertId}/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    /**
     * 1. [POST] 대기열 진입 (k6 부하 테스트 호환 모드)
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(
            @PathVariable Long concertId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request // 🌟 가상 부하테스트 쿠키 파싱용 추가
    ) {
        Long memberId;

        if (userDetails != null) {
            memberId = userDetails.getMemberId();
        } else {
            // 🌟 만약 k6 가상 유저라면? 헤더에서 Cookie 값을 읽어 가상 memberId를 추출합니다.
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null && cookieHeader.contains("VIRTUAL_SESSION_MEMBER_")) {
                String idStr = cookieHeader.split("VIRTUAL_SESSION_MEMBER_")[1];
                memberId = Long.parseLong(idStr);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "failed", "message", "로그인이 필요합니다."));
            }
        }

        WaitingStatusResponseDto response = waitingService.joinQueue(concertId, memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. [GET] 대기 상태 조회 (k6 부하 테스트 호환 모드)
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(
            @PathVariable Long concertId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request
    ) {
        Long memberId;

        if (userDetails != null) {
            memberId = userDetails.getMemberId();
        } else {
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null && cookieHeader.contains("VIRTUAL_SESSION_MEMBER_")) {
                String idStr = cookieHeader.split("VIRTUAL_SESSION_MEMBER_")[1];
                memberId = Long.parseLong(idStr);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "failed", "message", "로그인이 필요합니다."));
            }
        }

        WaitingStatusResponseDto response = waitingService.getStatus(concertId, memberId);
        return ResponseEntity.ok(response);
    }
    /**
     * 3. [POST] 대기열 강제 퇴장 (k6 부하 테스트 전용 비동기 이탈 API)
     */
    @PostMapping("/leave")
    public ResponseEntity<?> leaveQueue(
            @PathVariable Long concertId,
            HttpServletRequest request
    ) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null && cookieHeader.contains("VIRTUAL_SESSION_MEMBER_")) {
            String idStr = cookieHeader.split("VIRTUAL_SESSION_MEMBER_")[1];
            Long memberId = Long.parseLong(idStr);

            // DB 조회 없이 Redis 장부만 즉시 삭제!
            waitingService.clearUser(concertId, memberId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "대기열 반납 완료"));
        }
        return ResponseEntity.badRequest().body(Map.of("status", "failed"));
    }
}