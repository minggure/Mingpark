package com.example.mingpark.domain;

<<<<<<< HEAD
public class Reservation {

}
=======
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

//테스트용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reservation")
@Getter
public class Reservation {
    @Id // // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto Increment!
    @Column(name = "reservation_id")
    private Long id;

    // 🔗 FK 1: 회원 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 🔗 FK 2: 공연 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    // 🔗 FK 3: 좌석 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @Column(name = "hold_expired_at")
    private LocalDateTime holdExpiredAt;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Reservation(Member member, Concert concert, Seat seat, ReservationStatus status, int totalPrice, LocalDateTime reservedAt) {
        this.member = member;
        this.concert = concert;
        this.seat = seat;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservedAt = reservedAt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}





>>>>>>> origin/dev
