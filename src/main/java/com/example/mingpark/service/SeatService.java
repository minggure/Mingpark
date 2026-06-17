package com.example.mingpark.service;

import com.example.mingpark.domain.Seat;
import com.example.mingpark.dto.SeatResponseDto;
import com.example.mingpark.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final에 대하여 자동으로 생성자를 만들어줌 (보이진 않음)
@Transactional (readOnly = true)

public class SeatService {
    private final SeatRepository seatRepository;

    /**
     * 특정 공연에 해당하는 모든 좌석의 상태를 조회한다.
     *
     * @param concertId 조회할 공연의 식별자 (PK)
     * @return 해당 공연의 전체 좌석 정보 리스트 반환
     */
    public List<SeatResponseDto> getSeatsByConcertId(Long concertId){

        List<Seat> rawSeats = seatRepository.findAllByConcert_ConcertId(concertId);


        /**
         * rawSeats 는 현재 좌석 전체
         * stream() 처리할 목록을 일렬로 정리
         * map() 정리한 데이터를 하나씩 가져와 객체 생성
         * collect 다시 모아서 담는것
         */
        return rawSeats.stream()
                .map(SeatResponseDto::new)
                .collect(Collectors.toList());
    }

}
