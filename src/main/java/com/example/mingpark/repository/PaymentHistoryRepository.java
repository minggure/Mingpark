package com.example.mingpark.repository;

import com.example.mingpark.domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {


    @Modifying
    @Query("DELETE FROM PaymentHistory p WHERE p.reservation.concert.concertId = :concertId")
    void deleteAllByConcertId(@Param("concertId") Long concertId);
}