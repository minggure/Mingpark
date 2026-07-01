package com.example.mingpark.service;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.PaymentHistory;
import com.example.mingpark.domain.PaymentStatus;
import com.example.mingpark.domain.PaymentType;
import com.example.mingpark.domain.Reservation;
import com.example.mingpark.domain.ReservationStatus;
import com.example.mingpark.domain.SeatStatus;
import com.example.mingpark.event.PaymentFailedEvent;
import com.example.mingpark.event.PaymentSucceededEvent;
import com.example.mingpark.repository.PaymentHistoryRepository;
import com.example.mingpark.repository.ReservationRepository;
import com.example.mingpark.facade.HoldLockFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = IllegalStateException.class) // 예외가 발생해도 DB저장을 롤백하지않는다.
public class PaymentService {

    private final ReservationRepository reservationRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final HoldLockFacade holdLockFacade;

    public void payment(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매내역이 없습니다."));

        Member member = reservation.getMember();

        if (!member.getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 예약만 결제할 수 있습니다.");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태인 예매만 결제할 수 있습니다.");
        } // 예매 상태가 PENDING이지않으면 결제 못 함
        int price = reservation.getTotalPrice();

        if (member.getPoint() < price) {
            PaymentHistory history = PaymentHistory.builder()
                    .member(member)
                    .reservation(reservation)
                    .amount(price)
                    .type(PaymentType.PAYMENT)
                    .status(PaymentStatus.FAILED)
                    .build();

            paymentHistoryRepository.save(history);

            reservation.failPayment();
            reservation.getSeat().changeStatus(SeatStatus.AVAILABLE);
            holdLockFacade.releaseHold(reservation.getSeat().getId());

            eventPublisher.publishEvent(
                    new PaymentFailedEvent(reservation.getId(), member.getId(), price, "포인트 부족")
            );

            throw new IllegalStateException("포인트 부족");
        }

        member.decreasePoint(price);
        reservation.completePayment();
        reservation.getSeat().changeStatus(SeatStatus.RESERVED);

        PaymentHistory history = PaymentHistory.builder()
                .member(member)
                .reservation(reservation)
                .amount(price)
                .type(PaymentType.PAYMENT)
                .status(PaymentStatus.SUCCESS)
                .build();

        paymentHistoryRepository.save(history);

        holdLockFacade.releaseHold(reservation.getSeat().getId());

        eventPublisher.publishEvent(
                new PaymentSucceededEvent(reservation.getId(), member.getId(), price)
        );
    }

    public void fail(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역이 없습니다."));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 예약만 실패 처리할 수 있습니다.");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태인 예매만 실패 처리할 수 있습니다.");
        }
        int price = reservation.getTotalPrice();

        reservation.failPayment();
        reservation.getSeat().changeStatus(SeatStatus.AVAILABLE);

        PaymentHistory history = PaymentHistory.builder()
                .member(reservation.getMember())
                .reservation(reservation)
                .amount(price)
                .type(PaymentType.PAYMENT)
                .status(PaymentStatus.FAILED)
                .build();

        paymentHistoryRepository.save(history);

        holdLockFacade.releaseHold(reservation.getSeat().getId());

        eventPublisher.publishEvent(
                new PaymentFailedEvent(
                        reservation.getId(),
                        reservation.getMember().getId(),
                        price,
                        "결제 실패 처리"
                )
        );
    }
}
