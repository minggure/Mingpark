package com.example.mingpark.service;


import com.example.mingpark.domain.Concert;
import com.example.mingpark.domain.ConcertStatus;
import com.example.mingpark.domain.Seat;
import com.example.mingpark.domain.SeatStatus;
import com.example.mingpark.dto.ConcertCreatRequestDto;
import com.example.mingpark.dto.ConcertDetailResponseDto;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.exception.ConcertNotFoundException;
import com.example.mingpark.repository.ConcertRepository;
import com.example.mingpark.repository.PaymentHistoryRepository;
import com.example.mingpark.repository.ReservationRepository;
import com.example.mingpark.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 공연 정보 및 연동 좌석 생성·조회 비즈니스 로직 서비스.
 */
@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    /**
     * 페이지로부터 전달 받은 공연 등록 정보(DTO)를 받아 공연 엔티티를 생성하여 DB에 저장합니다
     * 생성시 공연의 기본 상태는 ON_SALE로 고정
     * @param request 등록할 공연 상세 정보 DTO
     */
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
                .image(request.getImage())
                .build();

        concertRepository.save(concert);
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            seats.add(Seat.builder()
                    .concert(concert)       // 방금 저장한 공연 객체 연결
                    .seatNumber(i)          // 1번부터 50번까지
                    .status(SeatStatus.AVAILABLE) // 기본 상태는 예매 가능
                    .build());
        }
        // 3. 50개의 좌석 한 번에 DB 저장
        seatRepository.saveAll(seats);
    }// 입력 받은 정보를 DB에 자동으로 넣어줌

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
     * 고유 식별 ID 기반 특정 공연 상세 정보 단일 조회 처리.
     * @param concertId 조회할 공연의 고유 식별자 (PK)
     * @return 공연 상세 정보가 담긴 Dto 반환
     */
    public ConcertDetailResponseDto getConcertDetail(Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new ConcertNotFoundException(concertId));

        return new ConcertDetailResponseDto(concert);
    }

    /**
     * 공연 삭제 API Service 구현 Transactional 을 사용해야 삭제 쿼리가 한번에 실행됨
     * 삭제 순서 중요 자식부터 삭제해서 차근히 하나씩 삭제해야함
     * 결제 내역 -> 예매 내역 -> 좌석 -> 공연 순서로 삭제
     * @param concertId
     */
    @Transactional
    public void deleteConcert(Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new ConcertNotFoundException(concertId));


        paymentHistoryRepository.deleteAllByConcertId(concertId); // 1등: 결제 내역 삭제
        reservationRepository.deleteAllByConcertId(concertId);    // 2등: 예매 내역 삭제
        seatRepository.deleteAllByConcertId(concertId);           // 3등: 좌석 삭제
        concertRepository.delete(concert);                          // 마지막에 한번에 부모(공연) 삭제
    }

    /**
     * 공연 등록후 수정하는 API
     * @param concertId
     * @param request
     */
    @Transactional
    public void updateConcert(Long concertId, ConcertCreatRequestDto request) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new ConcertNotFoundException(concertId));

        concert.update(request);

    }
}
