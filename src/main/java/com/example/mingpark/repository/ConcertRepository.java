package com.example.mingpark.repository;


import com.example.mingpark.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;


// DB에서 데이터를 갖고오는 파일 
public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
