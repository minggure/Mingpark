package com.example.mingpark.service;

import com.example.mingpark.domain.Seat;
import com.example.mingpark.domain.SeatStatus;
import com.example.mingpark.dto.SeatResponseDto;
import com.example.mingpark.facade.HoldLockFacade;
import com.example.mingpark.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
/**
 * 데이터베이스 및 캐시 연동 좌석 상태 조회 서비스.
 */
@Service
@RequiredArgsConstructor // final에 대하여 자동으로 생성자를 만들어줌 (보이진 않음)
@Transactional (readOnly = true)

public class SeatService {
    private final SeatRepository seatRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * 특정 공연의 전체 기본 좌석 상태 조회 처리.
     *
     * @param concertId 조회할 공연의 식별자 (PK)
     * @return 변환된 좌석 응답 DTO 리스트 반환
     */
    public List<SeatResponseDto> getSeatsByConcertId(Long concertId){

        List<Seat> rawSeats = seatRepository.findAllByConcert_ConcertId(concertId);

        return rawSeats.stream()
                .map(SeatResponseDto::new)
                .collect(Collectors.toList());
    }
    /**
     * Redis 분산 락 및 임시 선점 상태를 실시간 결합한 좌석 현황 통합 조회 처리.
     *
     * @param concertId 타겟 공연 고유 식별 ID
     * @return 런타임 상태가 동적 동기화된 좌석 응답 DTO 리스트 반환
     */
    public List<SeatResponseDto> getSeats(Long concertId) {
        List<Seat> seats = seatRepository.findAllByConcert_ConcertId(concertId);

        return seats.stream().map(seat -> {

            String lockKey = "lock:seat:" + seat.getId();
            String currentStatus = seat.getStatus().name();

            if (seat.getStatus() != SeatStatus.RESERVED && Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
                currentStatus = "HOLD";
            }

            return new SeatResponseDto(seat.getId(), seat.getSeatNumber(), currentStatus);
        }).collect(Collectors.toList());
    }

}
