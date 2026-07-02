package com.example.mingpark.service;

import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import com.example.mingpark.dto.MyPageMainResponseDto;
import com.example.mingpark.domain.Member;
import com.example.mingpark.dto.MyReservationDetailResponseDto;
import com.example.mingpark.dto.MyReservationListResponseDto;
import com.example.mingpark.repository.MemberRepository;
import com.example.mingpark.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 마이페이지 정보 취합 및 프로필 데이터 가공 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 특정 회원 고유 ID 기반 마이페이지 메인용 프로필 및 자산 정보 요약 조회.
     *
     * @param memberId 회원 고유 식별 ID
     * @return 요약 응답 DTO 반환
     * @throws IllegalArgumentException 존재하지 않는 회원 ID일 경우 발생
     */
    public MyPageMainResponseDto getMyPageSummary(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return new MyPageMainResponseDto(member);
    }
    public List<MyReservationListResponseDto> getMyReservations(Long memberId) {
        return reservationRepository.findAllByMemberIdAndStatusWithConcertAndSeat(memberId, ReservationStatus.RESERVED)
                .stream()
                .map(MyReservationListResponseDto::new)
                .toList();
    }

    public MyReservationDetailResponseDto getMyReservationDetail(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndMemberIdAndStatusWithConcertAndSeat(
                        reservationId,
                        memberId,
                        ReservationStatus.RESERVED
                )
                .orElseThrow(() -> new IllegalArgumentException("예매 내역을 찾을 수 없습니다."));

        return new MyReservationDetailResponseDto(reservation);
    }

}
