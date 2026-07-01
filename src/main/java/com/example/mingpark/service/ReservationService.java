package com.example.mingpark.service;

import com.example.mingpark.domain.Concert;
import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import com.example.mingpark.domain.Seat;
import com.example.mingpark.repository.ConcertRepository;
import com.example.mingpark.repository.MemberRepository;
import com.example.mingpark.repository.ReservationRepository;
import com.example.mingpark.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    public Long createReservation(Long concertId, Long seatId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("공연 정보를 찾을 수 없습니다."));

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석 정보를 찾을 수 없습니다."));

        Reservation reservation = Reservation.builder()
                .member(member)
                .concert(concert)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .totalPrice(concert.getConcertPrice())
                .reservedAt(LocalDateTime.now())
                .build();
        // DB에 예매를 저장하는 코드
        Reservation savedReservation = reservationRepository.save(reservation);

        return savedReservation.getId();
    }
}