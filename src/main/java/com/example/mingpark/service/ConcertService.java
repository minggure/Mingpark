package com.example.mingpark.service;


import com.example.mingpark.domain.Concert;
import com.example.mingpark.domain.ConcertStatus;
import com.example.mingpark.dto.ConcertCreatRequestDto;
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

    /**
     * 전체 공연 목록을 보여줍니다.
     * @return 전체 공연 목록을 담은 DTO 리스트
     */

    public List<ConcertResponseDto> getAllConcerts(){
        List<Concert> concerts = concertRepository.findAll(); // 리스트 List<Concert> 라는 곳에 모든 Concert 테이블 데이터 넣음

        return concerts.stream()
                .map(ConcertResponseDto::new)
                .collect(Collectors.toList());
    }
    // 등록 화면에서 받은 기본 정보와 상세 정보를 하나의 공연 엔티티로 저장한다.
    public void createConcert(ConcertCreatRequestDto request){
        Concert concert = Concert.builder()
                .concertTitle(request.getConcertTitle())
                .concertDate(request.getConcertDate())
                .concertTime(request.getConcertTime())
                .concertPrice(request.getConcertPrice())
                .description(request.getDescription())
                .place(request.getPlace())
                .reservationStartAt(request.getReservationStartAt())
                .reservationEndAt(request.getReservationEndAt())
                // 예매 가능 기간 안에서만 실제 예매 가능으로 계산된다.
                .status(ConcertStatus.ON_SALE)
                .build();

        concertRepository.save(concert); // 입력 받은 정보를 DB에 자동으로 넣어줌
    }
    /**
     * 사용자에게 보여주는 공연의 전체목록 페이징처리
     *
     * @param page 조회할 페이지 번호 ( 0부터 시작 )
     * @param size 한 페이지에 노출할 공연 데이터의 개수
     * @return 페이징 처리된 공연 DTO 목록과 전체 페이지 정보가 담긴 page 객체
     */
    public Page<ConcertResponseDto> getConcerts(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("concertId").descending()); // ConcertId를 내림차순으로 정렬후 보여주기
        Page<Concert> concertPage = concertRepository.findAll(pageable);

        return concertPage.map(ConcertResponseDto::new);
    }

    // 상세 페이지 요청에 사용되는 공연 한 건을 ID로 조회한다.

    /**
     *
     * @param concertId 조회할 공연의 고유 식별자 (PK)
     * @return 공연 상세 정보가 담긴 Dto 반환
     */
    public ConcertDetailResponseDto getConcertDetail(Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new ConcertNotFoundException(concertId));

        return new ConcertDetailResponseDto(concert);
    }
}
