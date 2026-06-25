package com.example.mingpark.controller;

import com.example.mingpark.dto.SeatResponseDto;
import com.example.mingpark.facade.HoldLockFacade;
import com.example.mingpark.security.CustomUserDetails;
import com.example.mingpark.service.SeatReservationService;
import com.example.mingpark.service.SeatService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 좌석 통제 및 임시 선점 컨트롤러
 * API:
 * GET    /api/concerts/{concertId}/seats                   - 특정 공연의 전체 좌석 현황 조회 (FN-SEAT-01)
 * POST   /api/concerts/{concertId}/seats/{seatId}/hold     - 특정 좌석 5분 임시 선점 분산 락 (FN-SEAT-02)
 * DELETE /api/concerts/{concertId}/seats/{seatId}/hold     - 임시 선점 수동 해제 (FN-SEAT-03)
 */
@Slf4j
@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
@Validated
public class SeatController {
    private final SeatService seatService;
    private final SeatReservationService seatReservationService;
    private final HoldLockFacade holdLockFacade;
    /**
     * GET /api/concerts/{concertId}/seats
     * 특정 공연의 전체 좌석 현황 조회
     * @param concertId 조호할 공연의 고유 번호 (양수)
     * @return 200 OK 와 함께 해당공연의 모든 좌석 상태 정보 리스트를 반환
     */
    @GetMapping("/{concertId}/seats")
    public ResponseEntity<List<SeatResponseDto>> getSeats(
            @PathVariable @Positive(message = "공연 ID는 양수여야 합니다.") Long concertId) {
        List<SeatResponseDto> seats = seatService.getSeats(concertId);
        return ResponseEntity.ok(seats);
    }
    /**
     * POST /api/concerts/{concertId}/seats/{seatId}/hold
     * 특정 좌석 5분 임시 선점 분산 락
     *
     * 인증된 사용자만 좌석 선점(찜하기) 가능
     * Redis를 활용하여 원자적 분산 락 가드를 획득한 후 DB 상태를 HOLD로 변경
     *
     * @return 200 OK { status: success, message: 안내문구 }
     */
    @PostMapping("/{concertId}/seats/{seatId}/hold")
    public ResponseEntity<?> holdSeat(
            @PathVariable @Positive(message = "공연 ID는 양수여야 합니다.") Long concertId,
            @PathVariable @Positive(message = "좌석 ID는 양수여야 합니다.") Long seatId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        Long memberId = userDetails.getMemberId();
        log.info("[SeatController] 좌석 선점 요청 concertId={}, seatId={}, memberId={}", concertId, seatId, memberId);

        try {
            // 분산 락 및 DB HOLD 선점 처리를 원자적으로 일괄 처리
            holdLockFacade.holdSeat(seatId,memberId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "좌석 찜하기 성공! 5분 안에 결제를 완료해주세요."
            ));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failed",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[SeatController] 선점 중 서버 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            ));
        }
    }
    /**
     * DELETE /api/concerts/{concertId}/seats/{seatId}/hold
     * 임시 선점 수동 해제
     *
     * 유저가 선택했던 포도알 좌석을 수동으로 취소하거나 창을 닫을 때 선점 해제 처리
     * Redis 장부에서 키를 강제 회수하고 DB 상태를 AVAILABLE로 롤백
     */
//    @DeleteMapping("/{concertId}/seats/{seatId}/hold")
//    public ResponseEntity<?> releaseHold(
//            @PathVariable @Positive(message = "공연 ID는 양수여야 합니다.") Long concertId,
//            @PathVariable @Positive(message = "좌석 ID는 양수여야 합니다.") Long seatId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//
//        if (userDetails == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("message", "로그인이 필요합니다."));
//        }
//
//        Long memberId = userDetails.getMemberId();
//        log.info("[SeatController] 좌석 수동 해제 요청 concertId={}, seatId={}, memberId={}", concertId, seatId, memberId);
//
//        try {
//            // 내부 서비스나 파사드 구역에 수동 해제 로직을 호출합니다.
//            // (seatReservationService 혹은 holdService 등 프로젝트에 구현된 수동 해제 메서드명을 매핑해 주면 됩니다.)
//            seatReservationService.releaseHold(seatId, memberId);
//
//            return ResponseEntity.ok(Map.of(
//                    "status", "success",
//                    "message", "좌석 선택이 취소되어 예매 가능 상태로 돌아갔습니다."
//            ));
//        } catch (Exception e) {
//            log.error("[SeatController] 해제 중 서버 에러 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "status", "error",
//                    "message", "해제 처리 중 오류가 발생했습니다."
//            ));
//        }
//    }
}

