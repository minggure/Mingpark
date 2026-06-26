package com.example.mingpark.repository;

import com.example.mingpark.domain.Seat;
import com.example.mingpark.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 좌석 엔티티에 대한 DB 접근을 담당하는 인터페이스 가장 첫번째로 만듬
 */
public interface SeatRepository extends JpaRepository<Seat, Long> {
    /**
     * 공연 ID를 기준으로 해당 공연 좌석 전체 조회.
     *
     * @param concertId 공연 식별자
     * @return 해당 공연에 속한 좌석 엔티티 리스트 반환
     */
    List<Seat> findAllByConcert_ConcertId(Long concertId);

}
