package com.example.mingpark.dto;

import com.example.mingpark.domain.Seat;
import lombok.Getter;

/**
 * 좌석 정보 응답 DTO.
 */
@Getter
public class SeatResponseDto {
    private final Long seatId;
    private final int seatNumber;
    private String status;  // 좌석의 상태

    /**
     * Seat 엔티티 데이터를 응답 규격으로 변환 처리.
     *
     * @param seat 좌석 엔티티 객체
     */
    public SeatResponseDto(Seat seat) {
        this.seatId = seat.getId();
        this.seatNumber = seat.getSeatNumber();
        this.status = seat.getStatus().name();
    }
    /**
     * 개별 유효 속성을 기반으로 한 응답 규격 생성.
     *
     * @param id 좌석 고유 식별 ID
     * @param seatNumber 좌석 번호
     * @param currentStatus 현재 좌석 상태 문자열
     */
    public SeatResponseDto(Long id, int seatNumber, String currentStatus) {
        this.seatId = id;
        this.seatNumber = seatNumber;
        this.status = currentStatus;
    }

    /**
     * 런타임 상태 값을 변경 처리.
     *
     * @param status 변경할 새 좌석 상태 값
     */
    public void changeStatus(String status) {
        this.status = status;
    }
}