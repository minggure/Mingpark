package com.example.mingpark.controller;

import com.example.mingpark.security.CustomUserDetails;
import com.example.mingpark.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{concertId}/seats/{seatId}/reservations")
    public ResponseEntity<?> createReservation(
            @PathVariable Long concertId,
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "failed",
                            "message", "로그인이 필요합니다."
                    ));
        }

        try {
            Long reservationId = reservationService.createReservation(
                    concertId,
                    seatId,
                    userDetails.getMemberId()
            );

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "예매가 생성되었습니다.",
                    "reservationId", reservationId
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failed",
                    "message", e.getMessage()
            ));
        }
    }
}