package com.example.mingpark.service;


import com.example.mingpark.domain.Concert;
import com.example.mingpark.domain.ConcertStatus;
import com.example.mingpark.dto.ConcertCreatRequesstDto;
import com.example.mingpark.dto.ConcertDetailResponseDto;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.exception.ConcertNotFoundException;
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
    // 등록 화면에서 받은 기본 정보와 상세 정보를 하나의 공연 엔티티로 저장한다.
    public void createConcert(ConcertCreatRequesstDto request){
        Concert concert = Concert.builder()
                .concertTitle(request.getConcertTitle())
                .concertDate(request.getConcertDate())
                .concertTime(request.getConcertTime())
                .concertPrice(request.getConcertPrice())
                // 이미지 파일 자체가 아니라 업로드 API가 반환한 접근 경로를 저장한다.
                .image(request.getImage())
                .description(request.getDescription())
                .place(request.getPlace())
                .reservationStartAt(request.getReservationStartAt())
                .reservationEndAt(request.getReservationEndAt())
                // 예매 가능 기간 안에서만 실제 예매 가능으로 계산된다.
                .status(ConcertStatus.ON_SALE)
                .build();

        concertRepository.save(concert);
    }
    // 목록 화면의 페이징 요청을 최신 등록 공연 순서로 조회한다.
    public Page<ConcertResponseDto> getConcerts(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("concertId").descending());
        Page<Concert> concertPage = concertRepository.findAll(pageable);

        return concertPage.map(ConcertResponseDto::new);
    }

    // 상세 페이지 요청에 사용되는 공연 한 건을 ID로 조회한다.
    public ConcertDetailResponseDto getConcertDetail(Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new ConcertNotFoundException(concertId));

        return new ConcertDetailResponseDto(concert);
    }
}
