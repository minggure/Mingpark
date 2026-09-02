package com.example.mingpark.event;

public record PaymentSucceededEvent(
        Long reservationId,
        Long memberId,
        int amount
) {
}