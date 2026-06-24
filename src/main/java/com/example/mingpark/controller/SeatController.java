package com.example.mingpark.controller;

import com.example.mingpark.dto.SeatResponseDto;
import com.example.mingpark.facade.HoldLockFacade;
import com.example.mingpark.security.CustomUserDetails;
import com.example.mingpark.service.SeatReservationService;
import com.example.mingpark.service.SeatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
@Validated
public class SeatController {
    private final SeatService seatService;
    private final SeatReservationService seatReservationService;
    private final HoldLockFacade holdLockFacade;
    /**
     * [GET] 특정 공연의 전체 좌석 상태를 조회
     * @param concertId 조호할 공연의 고유 번호 (양수 여야함)
     * @return 200 OK 와 함께 해당공연의 모든 좌석 상태 정보 리스트를 반환
     */
    @GetMapping("/{concertId}/seats")
    public ResponseEntity<List<SeatResponseDto>> getSeats(
            @PathVariable @Positive(message = "공연 ID는 양수여야 합니다.") Long concertId) {
        List<SeatResponseDto> seats = seatService.getSeatsByConcertId(concertId);
        for (SeatResponseDto seat : seats) {
            String occupant = seatReservationService.getSeatOccupant(concertId, seat.getSeatNumber());
            if (occupant != null) {
                seat.changeStatus("HOLD");
            }
        }
        return ResponseEntity.ok(seats);
    }
    @PostMapping("/{concertId}/seats/{seatId}/hold")
    public ResponseEntity<?> holdSeat(
            @PathVariable Long concertId,
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 1. 시큐리티 가드를 통한 인증 상태 검증
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }

        Long memberId = userDetails.getMemberId();

        try {
            // 2. 파사드를 호출하여 원자적 분산 락 연산 시작
            holdLockFacade.holdSeat(seatId, memberId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "좌석 찜하기 성공! 5분 안에 결제를 완료해주세요."
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failed",
                    "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failed",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            ));
        }
    }
}

