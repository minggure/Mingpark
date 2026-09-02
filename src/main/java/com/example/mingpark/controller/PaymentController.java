package com.example.mingpark.controller;

import com.example.mingpark.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mingpark.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{reservationId}/payment")
    public ResponseEntity<?> payment(
            @PathVariable Long reservationId,
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
            paymentService.payment(reservationId, userDetails.getMemberId());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "결제 완료"
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failed",
                    "message", e.getMessage()
            ));
        }
    }
    // 결제 실패 API
    @PostMapping("/{reservationId}/fail")
    public ResponseEntity<?> fail(
            @PathVariable Long reservationId,
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
            paymentService.fail(reservationId, userDetails.getMemberId());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "결제 실패 처리 완료"
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failed",
                    "message", e.getMessage()
            ));
        }
    }
}
