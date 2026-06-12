package com.example.mingpark.dto;

import com.example.mingpark.domain.Concert;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 공연 상세 화면에 필요한 정보를 전달하는 응답 DTO.
 * 목록 응답과 분리해 설명, 장소, 예매 기간 같은 상세 정보만 이곳에서 관리한다.
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

    // 엔티티를 화면에 직접 노출하지 않고 상세 화면 전용 데이터로 변환한다.
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
