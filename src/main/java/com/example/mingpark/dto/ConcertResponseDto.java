package com.example.mingpark.dto;


import com.example.mingpark.domain.Concert;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ConcertResponseDto {
    private Long concertId;
    private String concertTitle;
    private String image;
    private LocalTime concertTime;
    private LocalDate concertDate;
    private int concertPrice;


    public ConcertResponseDto(Concert concert) {
        this.concertId = concert.getConcertId();
        this.concertTitle = concert.getConcertTitle();
        this.image = concert.getImage();
        this.concertPrice = concert.getConcertPrice();
        this.concertTime = concert.getConcertTime();
        this.concertDate = concert.getConcertDate();


    }
}
