package com.example.mingpark.dto;


import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 마이페이지 내 예매 내역 목록 화면에 표시할 예매 요약 응답 DTO.
 *
 * <p>예매 엔티티와 연결된 공연 정보를 함께 사용하여 목록 카드에 필요한
 * 공연명, 이미지, 예매 번호, 관람 일시, 장소, 예매 상태를 전달한다.</p>
 */
@Getter
public class MyReservationListResponseDto {
    private final Long reservationId;
    private final String reservationNumber;
    private final String concertTitle;
    private final String concertImage;
    private final LocalDate concertDate;
    private final LocalTime concertTime;
    private final String place;
    private final ReservationStatus status;

    /**
     * 예매 엔티티를 목록 화면 응답 형식으로 변환한다.
     *
     * @param reservation 로그인 사용자의 예매 정보 엔티티
     */
    public MyReservationListResponseDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.reservationNumber = "R" + reservation.getCreatedAt().toLocalDate().toString().replace("-", "")
                + String.format("%05d", reservation.getId());
        this.concertTitle = reservation.getConcert().getConcertTitle();
        this.concertImage = reservation.getConcert().getImage();
        this.concertDate = reservation.getConcert().getConcertDate();
        this.concertTime = reservation.getConcert().getConcertTime();
        this.place = reservation.getConcert().getPlace();
        this.status = reservation.getStatus();
    }
}
