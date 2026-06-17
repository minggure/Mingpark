package com.example.mingpark.dto;

import com.example.mingpark.domain.Seat;
import lombok.Getter;

@Getter
/**
 * 사용자에게 좌석의 상태 정보를 전달하기위한 데이터 전송 객체 (DTO) 상자로 많이 부름
 */
public class SeatResponseDto {
    private final Long seatId;
    private final int seatNumber;
    private final String status;  // 좌석의 상태

    /**
     * 엔티티를 DTO로 변환하기 위한 생성자
     * @param seat 좌석 엔티티
     */
    public SeatResponseDto(Seat seat) {
        this.seatId = seat.getId();
        this.seatNumber = seat.getSeatNumber();
        this.status = seat.getStatus().name();
    }
}