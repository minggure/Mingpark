package com.example.mingpark.dto;


import com.example.mingpark.domain.Concert;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter

// 공연 목록 카드에 필요한 요약 정보만 전달한다.
public class ConcertResponseDto {
    private Long concertId;
    private String concertTitle;
    private String image;
    private String place;
    private LocalTime concertTime;
    private LocalDate concertDate;
    private int concertPrice;

    // Concert 엔티티를 목록 화면용 응답으로 변환한다.
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
