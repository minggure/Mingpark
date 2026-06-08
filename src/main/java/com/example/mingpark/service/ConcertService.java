package com.example.mingpark.service;


import com.example.mingpark.domain.Concert;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;

    // 공연 목록 전체 가져오기
    public List<ConcertResponseDto> getAllConcerts(){
        List<Concert> concerts = concertRepository.findAll();

        return concerts.stream()
                .map(ConcertResponseDto::new)
                .collect(Collectors.toList());
    }
}
