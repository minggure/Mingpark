package com.example.mingpark.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "concerts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long concertId;

    @Column(name = "concert_title", nullable = false)
    private String concertTitle;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "concert_time", nullable = false)
    private LocalTime concertTime;

    @Column(name = "concert_date", nullable = false)
    private LocalDate concertDate;

    @Column(name = "concert_price", nullable = false)
    private int concertPrice;

    // 상세 페이지에서 보여줄 공연 소개와 장소
    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "place")
    private String place;

    // 예매 버튼 활성화 여부를 판단할 때 사용하는 예매 가능 기간
    @Column(name = "reservation_start_at")
    private LocalDateTime reservationStartAt;

    @Column(name = "reservation_end_at")
    private LocalDateTime reservationEndAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ConcertStatus status = ConcertStatus.UPCOMING;

    public Concert(String concertTitle, String image, LocalTime concertTime, LocalDate concertDate, int concertPrice) {
        this.concertTitle = concertTitle;
        this.image = image;
        this.concertTime = concertTime;
        this.concertDate = concertDate;
        this.concertPrice = concertPrice;
    }

    public Concert(String concertTitle, String image, LocalTime concertTime, LocalDate concertDate, int concertPrice, ConcertStatus status) {
        this.concertTitle = concertTitle;
        this.image = image;
        this.concertTime = concertTime;
        this.concertDate = concertDate;
        this.concertPrice = concertPrice;
        this.status = status;
    }



    public void changeStatus(ConcertStatus status) {
        this.status = status;
    }

    /**
     * 공연 상태와 예매 기간을 함께 확인해 현재 예매 가능한 공연인지 판단한다.
     * 예매 기간이 등록되지 않은 기존 공연은 예매 불가능으로 처리한다.
     */
    public boolean isReservationAvailable() {
        if (status != ConcertStatus.ON_SALE
                || reservationStartAt == null
                || reservationEndAt == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(reservationStartAt) && !now.isAfter(reservationEndAt);
    }
}
