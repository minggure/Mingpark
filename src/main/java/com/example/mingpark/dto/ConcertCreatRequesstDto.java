package com.example.mingpark.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
// 공연 등록 화면에서 JSON으로 전달하는 기본 정보와 상세 페이지용 정보를 함께 받는다.
public class ConcertCreatRequesstDto {
    private String concertTitle;
    private LocalTime concertTime;
    private LocalDate concertDate;
    private int concertPrice;
    // 붙여넣기 이미지 업로드 API가 반환한 /uploads/... 경로
    private String image;
    private String description;
    private String place;
    private LocalDateTime reservationStartAt;
    private LocalDateTime reservationEndAt;
}
