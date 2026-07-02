package com.example.mingpark.repository;

import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

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
