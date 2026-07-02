package com.example.mingpark.dto;

import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyReservationDetailResponseDto {
    private final Long reservationId;
    private final String reservationNumber;
    private final String concertTitle;
    private final String concertImage;
    private final int seatNumber;
    private final int totalPrice;
    private final ReservationStatus status;
    private final LocalDateTime reservedAt;
    private final LocalDateTime confirmedAt;
    private final LocalDateTime cancelledAt;
    private final LocalDateTime createdAt;
    private final boolean cancellable;

    public MyReservationDetailResponseDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.reservationNumber = "R" + reservation.getCreatedAt().toLocalDate().toString().replace("-", "")
                + String.format("%05d", reservation.getId());
        this.concertTitle = reservation.getConcert().getConcertTitle();
        this.concertImage = reservation.getConcert().getImage();
        this.seatNumber = reservation.getSeat().getSeatNumber();
        this.totalPrice = reservation.getTotalPrice();
        this.status = reservation.getStatus();
        this.reservedAt = reservation.getReservedAt();
        this.confirmedAt = reservation.getConfirmedAt();
        this.cancelledAt = reservation.getCancelledAt();
        this.createdAt = reservation.getCreatedAt();

        LocalDateTime concertDateTime = LocalDateTime.of(
                reservation.getConcert().getConcertDate(),
                reservation.getConcert().getConcertTime()
        );
        this.cancellable = reservation.getStatus() == ReservationStatus.RESERVED
                && concertDateTime.isAfter(LocalDateTime.now());
    }
}
