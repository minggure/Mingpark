package com.example.mingpark.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "concerts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
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
}
