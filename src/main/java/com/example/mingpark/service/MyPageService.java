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
import com.example.mingpark.domain.PaymentHistory;
import com.example.mingpark.domain.PaymentStatus;
import com.example.mingpark.domain.PaymentType;
import com.example.mingpark.domain.SeatStatus;
import com.example.mingpark.repository.PaymentHistoryRepository;
import java.time.LocalDateTime;

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
    private final PaymentHistoryRepository paymentHistoryRepository;

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
    /**
     * 로그인한 회원의 예매 완료 내역 목록을 조회한다.
     *
     * <p>마이페이지 목록에서는 결제 완료된 예매만 보여주기 위해
     * {@link ReservationStatus#RESERVED} 상태만 조회한다.</p>
     *
     * @param memberId 로그인한 회원 ID
     * @return 예매 완료 내역 목록 응답 DTO 리스트
     */
    public List<MyReservationListResponseDto> getMyReservations(Long memberId) {
        // RESERVED 상태인 예매만 리스트 노출
        return reservationRepository.findAllByMemberIdAndStatusWithConcertAndSeat(memberId, ReservationStatus.RESERVED)
                .stream()
                .map(MyReservationListResponseDto::new)
                .toList();
    }

    /**
     * 로그인한 회원의 예매 완료 상세 정보를 조회한다.
     *
     * <p>예매 ID와 회원 ID를 함께 검증하여 본인의 예매 내역만 조회할 수 있게 한다.</p>
     *
     * @param memberId 로그인한 회원 ID
     * @param reservationId 조회 대상 예매 ID
     * @return 예매 상세 응답 DTO
     * @throws IllegalArgumentException 조회 가능한 예매 내역이 없을 경우
     */
    public MyReservationDetailResponseDto getMyReservationDetail(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndMemberIdAndStatusWithConcertAndSeat(
                        reservationId,
                        memberId,
                        ReservationStatus.RESERVED
                )
                .orElseThrow(() -> new IllegalArgumentException("예매 내역을 찾을 수 없습니다."));

        return new MyReservationDetailResponseDto(reservation);
    }

    /**
     * 로그인한 회원의 예매를 환불 처리한다.
     *
     * <p>예매 상태가 {@link ReservationStatus#RESERVED}이고 공연 시작 전인 경우에만
     * 포인트를 복구하고, 예매 상태를 취소로 변경하며, 좌석을 예매 가능 상태로 되돌린다.
     * 환불 내역은 {@link PaymentType#REFUND} 타입의 결제 이력으로 저장한다.</p>
     *
     * @param memberId 로그인한 회원 ID
     * @param reservationId 환불 대상 예매 ID
     * @throws IllegalArgumentException 환불 가능한 예매 내역이 없을 경우
     * @throws IllegalStateException 공연 시작 이후라 환불할 수 없을 경우
     */
    @Transactional
    public void refundReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndMemberIdAndStatusWithConcertAndSeat(
                        reservationId,
                        memberId,
                        ReservationStatus.RESERVED
                )
                .orElseThrow(() -> new IllegalArgumentException("환불 가능한 예매 내역이 없습니다."));

        LocalDateTime concertDateTime = LocalDateTime.of(
                reservation.getConcert().getConcertDate(),
                reservation.getConcert().getConcertTime()
        );

        if (!concertDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("공연 시작 이후에는 환불할 수 없습니다.");
        }

        Member member = reservation.getMember();
        int refundAmount = reservation.getTotalPrice();

        member.addPoint(refundAmount);
        reservation.cancel();
        reservation.getSeat().changeStatus(SeatStatus.AVAILABLE);

        PaymentHistory refundHistory = PaymentHistory.builder()
                .member(member)
                .reservation(reservation)
                .amount(refundAmount)
                .type(PaymentType.REFUND)
                .status(PaymentStatus.SUCCESS)
                .build();

        paymentHistoryRepository.save(refundHistory);
    }

}
