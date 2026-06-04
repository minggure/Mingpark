package com.example.mingpark.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 규격 유지
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_history_id") // 고유 PK ID
    private Long id;

    // 👤 [회원 ➡️ 결제이력] 1:N 비식별 관계
    // 윤탱이가 만든 Member 엔티티와 연관 관계를 맺어주는 부분이야!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 🎫 [예매 ➡️ 결제이력] 1:N 비식별 관계 (영수증 연결고리)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private int amount; // 변동 금액 (-60000, +60000 등)

    @Column(nullable = false, length = 50)
    private String type; // PAYMENT(결제), REFUND(환불)

    @Column(nullable = false, length = 50)
    private String status; // SUCCESS, FAILED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 결제 이력을 생성할 때 사용할 생성자 (윤탱이의 Member 생성자 스타일과 통일!)
    public PaymentHistory(Member member, Reservation reservation, int amount, String type, String status) {
        this.member = member;
        this.reservation = reservation;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.createdAt = LocalDateTime.now(); // 생성될 때 현재 시간이 자동으로 기록됨!
    }
}