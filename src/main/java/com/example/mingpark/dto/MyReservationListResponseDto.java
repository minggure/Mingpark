package com.example.mingpark.dto;


import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

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
