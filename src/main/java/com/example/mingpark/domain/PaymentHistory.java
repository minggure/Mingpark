package com.example.mingpark.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 객체 생성 방지
public class PaymentHistory {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AutoIncrement 설정
    @Column(name = "payment_history_id")
    private Long id;

    // 👤 [회원 ➡️ 결제이력] 1:N 비식별 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 🎫 [예매 ➡️ 결제이력] 1:N 비식별 관계
    // TODO: 다른 팀원이 예매(Reservation) 엔티티 완성하면 이 부분 패키지 경로 및 조율 필요!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private int amount; // 변동된 포인트 (예: -60000, +60000)

    @Enumerated(EnumType.STRING) // DB에 문자열(PAYMENT, REFUND)로 저장되도록 설정
    @Column(nullable = false, length = 20)
    private PaymentType type; // 결제 타입 (Enum)

    @Enumerated(EnumType.STRING) // DB에 문자열(SUCCESS, FAILED)로 저장되도록 설정
    @Column(nullable = false, length = 20)
    private PaymentStatus status; // 결제 상태 (Enum)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 처리 일시

    //생성자
    public PaymentHistory(Member member, Reservation reservation, int amount, PaymentType type, PaymentStatus status) {
        this.member = member;
        this.reservation = reservation;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.createdAt = LocalDateTime.now(); // 생성되는 순간의 시간이 자동으로 기록됨!
    }
}