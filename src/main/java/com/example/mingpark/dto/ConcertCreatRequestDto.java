package com.example.mingpark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
/**
 * 공연 등록 요청 DTO.
 */
@Getter
@RequiredArgsConstructor
// 공연 등록 화면에서 JSON으로 전달하는 기본 정보와 상세 페이지용 정보를 함께 받는다.
public class ConcertCreatRequestDto {
    @NotBlank
    @Size(max = 255)
    private final String concertTitle;

    @NotNull
    private final LocalTime concertTime;

    @NotNull
    private final LocalDate concertDate;

    @PositiveOrZero
    private final int concertPrice;

    private final String image; // 붙여넣기 이미지 업로드 API가 반환한 /uploads/... 경로

    @Size(max = 2000)
    private final String description;

    @Size(max = 255)
    private final String place;

    private final LocalDateTime reservationStartAt;
    private final LocalDateTime reservationEndAt;

}
