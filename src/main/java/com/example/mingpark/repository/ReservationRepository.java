package com.example.mingpark.repository;

import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 예매 정보 조회 및 저장을 담당하는 Repository.
 *
 * <p>마이페이지 예매 내역 조회에서는 공연과 좌석 정보가 함께 필요하므로
 * fetch join으로 연관 엔티티를 한 번에 조회한다.</p>
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 특정 회원의 특정 상태 예매 목록을 공연 및 좌석 정보와 함께 조회한다.
     *
     * @param memberId 조회 대상 회원 ID
     * @param status 조회할 예매 상태
     * @return 회원의 예매 목록. 최신 생성순으로 정렬된다.
     */
    @Query("""
    select r from Reservation r
    join fetch r.concert
    join fetch r.seat
    where r.member.id = :memberId
      and r.status = :status
    order by r.createdAt desc
""")
    List<Reservation> findAllByMemberIdAndStatusWithConcertAndSeat(
            @Param("memberId") Long memberId,
            @Param("status") ReservationStatus status
    );

    /**
     * 특정 회원의 단일 예매 상세를 공연 및 좌석 정보와 함께 조회한다.
     *
     * <p>reservationId와 memberId를 함께 조건으로 사용하여 다른 사용자의
     * 예매 내역 접근을 차단한다.</p>
     *
     * @param reservationId 조회 대상 예매 ID
     * @param memberId 로그인한 회원 ID
     * @param status 조회할 예매 상태
     * @return 조건에 맞는 예매가 있으면 Optional에 담아 반환
     */
    @Query("""
    select r from Reservation r
    join fetch r.concert
    join fetch r.seat
    where r.id = :reservationId
      and r.member.id = :memberId
      and r.status = :status
""")
    Optional<Reservation> findByIdAndMemberIdAndStatusWithConcertAndSeat(
            @Param("reservationId") Long reservationId,
            @Param("memberId") Long memberId,
            @Param("status") ReservationStatus status
    );
}
