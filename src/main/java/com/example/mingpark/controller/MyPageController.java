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
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;

/**
 * 마이페이지 컨트롤러
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

    /**
     * 로그인한 사용자의 예매 완료 내역 목록을 조회한다.
     *
     * @param userDetails Spring Security 인증 사용자 정보
     * @return 200 OK 및 예매 목록, 인증되지 않은 경우 401 응답
     */
    @GetMapping("/users/me/reservations")
    public ResponseEntity<?> getMyReservations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(myPageService.getMyReservations(userDetails.getMemberId()));
    }

    /**
     * 로그인한 사용자의 단일 예매 상세 정보를 조회한다.
     *
     * @param userDetails Spring Security 인증 사용자 정보
     * @param reservationId 조회 대상 예매 ID
     * @return 200 OK 및 예매 상세 정보, 인증되지 않은 경우 401 응답
     */
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

    /**
     * 로그인한 사용자의 예매를 환불 처리한다.
     *
     * <p>환불 가능 여부와 본인 예매 여부는 서비스 계층에서 다시 검증한다.</p>
     *
     * @param userDetails Spring Security 인증 사용자 정보
     * @param reservationId 환불 대상 예매 ID
     * @return 환불 성공 또는 실패 메시지
     */
    @PostMapping("/users/me/reservations/{reservationId}/refund")
    public ResponseEntity<?> refundReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reservationId
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            myPageService.refundReservation(userDetails.getMemberId(), reservationId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "환불이 완료되었습니다."
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "fail",
                    "message", e.getMessage()
            ));
        }
    }
}
