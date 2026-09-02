package com.example.mingpark.event;

public record PaymentFailedEvent(
        Long reservationId,
        Long memberId,
        int amount,
        String reason
) {
}
