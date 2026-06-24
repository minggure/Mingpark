package com.example.mingpark.controller;

import com.example.mingpark.domain.Member;
import com.example.mingpark.dto.SeatResponseDto;
import com.example.mingpark.facade.HoldLockFacade;
import com.example.mingpark.service.SeatReservationService;
import com.example.mingpark.service.SeatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
            @PathVariable @Positive(message = "공연 ID는 양수여야 합니다.") Long concertId){
        List<SeatResponseDto> seats = seatService.getSeatsByConcertId(concertId);
        //redis 도입하며 추가
        for (SeatResponseDto seat : seats) {
            String occupant = seatReservationService.getSeatOccupant(concertId, seat.getSeatNumber());
            // DB상에는 AVAILABLE(예매가능)이더라도, Redis에 선점되었다면 유저에게는 HOLD(선점됨)로 위장하여 응답
            if (occupant != null) {
                seat.changeStatus("HOLD");
            }
        }
        return ResponseEntity.ok(seats);
    }
    /**
     * [POST] 사용자가 특정 좌석을 클릭했을 때 파사드를 호출하여 5분 선점(분산 락)을 진행
     *
     * @param concertId 공연 고유 식별 ID
     * @param seatId 선택한 좌석의 고유 식별 PK
     * @param request 유저의 세션 검증을 위한 객체
     * @return 성공 시 200 OK, 실패 시 400 에러와 사유 반환
     */
    @PostMapping("/{concertId}/seats/{seatId}/occupy")
    public ResponseEntity<Map<String, String>> occupySeat(
            @PathVariable("concertId") @Positive(message = "공연 ID는 양수여야 합니다.") Long concertId,
            @PathVariable("seatId") @Positive(message = "좌석 ID는 양수여야 합니다.") Long seatId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        try {
            holdLockFacade.holdSeat(seatId, loginMember.getId());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "좌석이 5분간 임시 선점되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "fail",
                    "message", e.getMessage()
            ));
        }
    }
}

