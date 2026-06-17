package com.example.mingpark.controller;


import com.example.mingpark.dto.SeatResponseDto;
import com.example.mingpark.service.SeatService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
@Validated
public class SeatController {
    private final SeatService seatService;


    /**
     * [GET] 특정 공연의 전체 좌석 상태를 조회
     * @param concertId 조호할 공연의 고유 번호 (양수 여야함)
     * @return 200 OK 와 함께 해당공연의 모든 좌석 상태 정보 리스트를 밪ㄴ환
     */
    @GetMapping("/{concertId}/seats")
    public ResponseEntity<List<SeatResponseDto>> getSeats(
            @PathVariable @Positive(message = "공연 ID는 양수여야 합니다.") Long concertId){

        List<SeatResponseDto> seats = seatService.getSeatsByConcertId(concertId);

    return ResponseEntity.ok(seats);
}
}
