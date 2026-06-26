package com.example.mingpark.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
/**
 * 좌석 정보 엔티티
 * 공연별 개별 좌석 번호 및 해당 좌석의 예약 가능 상태 관리.
 * 동일 공연 내 좌석 번호 중복 방지를 위한 유니크 제약조건(uk_seats_concert_seat_number) 포함.
 */
@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seats_concert_seat_number",
                        columnNames = {"concert_id", "seat_number"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status = SeatStatus.AVAILABLE;


    @Builder
    public Seat(Concert concert, int seatNumber, SeatStatus status) {
        this.concert = concert;
        this.seatNumber = seatNumber;
        this.status = status;
    }
    /**
     * 좌석의 예약 상태를 변경 처리.
     *
     * @param status 변경할 좌석 상태 값 (AVAILABLE, HOLD, RESERVED 등)
     */
    public void changeStatus(SeatStatus status) {
        this.status = status;
    }
}

