package com.example.mingpark.dto;

import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 마이페이지 예매 내역 상세 화면에 표시할 예매 상세 응답 DTO.
 *
 * <p>공연 정보, 좌석 정보, 결제 금액, 예매 상태 시간, 취소 가능 여부를
 * 한 번에 내려주어 프론트 상세 페이지에서 바로 렌더링할 수 있게 한다.</p>
 */
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

    /**
     * 예매 엔티티를 상세 화면 응답 형식으로 변환한다.
     *
     * <p>취소 가능 여부는 예매 상태가 {@link ReservationStatus#RESERVED}이고
     * 공연 시작 시간이 현재 시간 이후인 경우에만 true로 계산한다.</p>
     *
     * @param reservation 로그인 사용자의 예매 정보 엔티티
     */
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
