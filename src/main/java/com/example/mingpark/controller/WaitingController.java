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

/**
 * 공연 예매 대기열 진입, 상태 조회 및 이탈 처리를 담당하는 REST 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts/{concertId}/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    /**
     * 특정 공연의 대기열 진입 요청 처리.
     *
     * @param concertId 공연 고유 식별 ID
     * @param userDetails 인증된 회원의 세션 정보
     * @param request 서블릿 리퀘스트 객체
     * @return 대기 상태 및 순위 응답 객체
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(
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

        WaitingStatusResponseDto response = waitingService.joinQueue(concertId, memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 공연 대기열 내 회원의 현재 대기 상태 및 순위 조회 처리.
     *
     * @param concertId 공연 고유 식별 ID
     * @param userDetails 인증된 회원의 세션 정보
     * @param request 서블릿 리퀘스트 객체
     * @return 실시간 대기 순위 및 상태 응답 객체
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
     * 부하 테스트 세션의 대기열 데이터 강제 해제 및 조기 이탈 처리.
     *
     * @param concertId 공연 고유 식별 ID
     * @param request 서블릿 리퀘스트 객체
     * @return 대기열 삭제 성공 또는 실패 응답 맵
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

            waitingService.clearUser(concertId, memberId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "대기열 반납 완료"));
        }
        return ResponseEntity.badRequest().body(Map.of("status", "failed"));
    }
}