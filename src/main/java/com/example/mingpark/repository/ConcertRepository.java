package com.example.mingpark.repository;


import com.example.mingpark.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 공연 정보 데이터베이스 접근 리포지토리.
 */
public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
