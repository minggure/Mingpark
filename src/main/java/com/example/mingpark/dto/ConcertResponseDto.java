package com.example.mingpark.dto;


import com.example.mingpark.domain.Concert;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 공연 목록 정보 응답 DTO.
 */
@Getter
public class ConcertResponseDto {
    private final Long concertId;
    private final String concertTitle;
    private final String image;
    private final String place;
    private final LocalTime concertTime;
    private final LocalDate concertDate;
    private final int concertPrice;

    /**
     * Concert 엔티티 데이터를 목록 응답 규격으로 변환 처리.
     *
     * @param concert 공연 엔티티 객체
     */
    public ConcertResponseDto(Concert concert) {
        this.concertId = concert.getConcertId();
        this.concertTitle = concert.getConcertTitle();
        this.image = concert.getImage();
        this.place = concert.getPlace();
        this.concertPrice = concert.getConcertPrice();
        this.concertTime = concert.getConcertTime();
        this.concertDate = concert.getConcertDate();


    }
}
