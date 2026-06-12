package com.example.mingpark.dto;


import com.example.mingpark.domain.Concert;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter

// 화면에 보여줄 5가지 클래스를 불러옴
public class ConcertResponseDto {
    private Long concertId;
    private String concertTitle;
    private String image;
    private LocalTime concertTime;
    private LocalDate concertDate;
    private int concertPrice;

// DB 에서 받아온 값을 넣어주는 작업
    public ConcertResponseDto(Concert concert) {
        this.concertId = concert.getConcertId();
        this.concertTitle = concert.getConcertTitle();
        this.image = concert.getImage();
        this.concertPrice = concert.getConcertPrice();
        this.concertTime = concert.getConcertTime();
        this.concertDate = concert.getConcertDate();


    }
}
