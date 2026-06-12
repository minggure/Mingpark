package com.example.mingpark.service;


import com.example.mingpark.domain.Concert;
import com.example.mingpark.domain.ConcertStatus;
import com.example.mingpark.dto.ConcertCreatRequesstDto;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    // 공연 추가 하기
    public void createConcert(ConcertCreatRequesstDto request){
        Concert concert = Concert.builder()
                .concertTitle(request.getConcertTitle())
                .concertDate(request.getConcertDate())
                .concertTime(request.getConcertTime())
                .concertPrice(request.getConcertPrice())
                .image(request.getImage())
                .status(ConcertStatus.valueOf("UPCOMING")) //기본값
                .build();

        concertRepository.save(concert);
    }
    public Page<ConcertResponseDto> getConcerts(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("concertId").descending());
        Page<Concert> concertPage = concertRepository.findAll(pageable);

        return concertPage.map(ConcertResponseDto::new);
    }
}
