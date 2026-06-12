package com.example.mingpark.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ConcertCreatRequesstDto {
    private String concertTitle;
    private LocalTime concertTime;
    private LocalDate concertDate;
    private int concertPrice;
    private String image;


}
