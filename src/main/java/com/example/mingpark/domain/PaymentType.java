package com.example.mingpark.domain;

/**
 * 포인트 결제 이력의 거래 유형.
 *
 * <p>PAYMENT는 포인트 차감, REFUND는 환불로 인한 포인트 복구를 의미한다.</p>
 */
public enum PaymentType {
    PAYMENT, // 결제 (포인트 차감)
    REFUND   // 환불 (포인트 복구)
}
