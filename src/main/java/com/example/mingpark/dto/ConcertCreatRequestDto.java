package com.example.mingpark.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
// 공연 등록 화면에서 JSON으로 전달하는 기본 정보와 상세 페이지용 정보를 함께 받는다.
public class ConcertCreatRequestDto {
    private final String concertTitle;
    private final LocalTime concertTime;
    private final LocalDate concertDate;
    private final int concertPrice;
    private final String image; // 붙여넣기 이미지 업로드 API가 반환한 /uploads/... 경로
    private final String description;
    private final String place;
    private final LocalDateTime reservationStartAt;
    private final LocalDateTime reservationEndAt;

}
