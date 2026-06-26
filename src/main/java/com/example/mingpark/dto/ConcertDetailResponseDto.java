package com.example.mingpark.dto;

import com.example.mingpark.domain.Concert;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 공연 상세 정보 응답 DTO.
 */
@Getter
public class ConcertDetailResponseDto {

    private final Long concertId;
    private final String concertTitle;
    private final String description;
    private final String place;
    private final String image;
    private final LocalDate concertDate;
    private final LocalTime concertTime;
    private final int concertPrice;
    private final LocalDateTime reservationStartAt;
    private final LocalDateTime reservationEndAt;
    private final boolean reservationAvailable;

    /**
     * Concert 엔티티 데이터를 응답 규격으로 변환 처리.
     *
     * @param concert 공연 엔티티 객체
     */
    public ConcertDetailResponseDto(Concert concert) {
        this.concertId = concert.getConcertId();
        this.concertTitle = concert.getConcertTitle();
        this.description = concert.getDescription();
        this.place = concert.getPlace();
        this.image = concert.getImage();
        this.concertDate = concert.getConcertDate();
        this.concertTime = concert.getConcertTime();
        this.concertPrice = concert.getConcertPrice();
        this.reservationStartAt = concert.getReservationStartAt();
        this.reservationEndAt = concert.getReservationEndAt();
        this.reservationAvailable = concert.isReservationAvailable();
    }
}
