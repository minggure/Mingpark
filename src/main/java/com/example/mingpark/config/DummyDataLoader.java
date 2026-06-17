package com.example.mingpark.config; // 패키지 이름은 네 프로젝트에 맞게!

import com.example.mingpark.domain.Concert;
import com.example.mingpark.domain.Seat;
import com.example.mingpark.domain.SeatStatus;
import com.example.mingpark.repository.ConcertRepository;
import com.example.mingpark.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
 * @Component: 스프링 컨테이너에 의해  직접 관리하게 하는것 ( 클래스를 bean 으로 설정 )
 * CommandLineRunner: 서버가 켜질때마다 run 메소드를 한번 실행하는것
 */
@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    /**
     * 더미데이터 50개를 만들어주는 메소드이다.
     * @param args 자동으로 생성됨
     * @throws Exception 50개의 좌석 데이터가 생성됨
     */
    @Override
    public void run(String... args) throws Exception {

      // 가짜 콘서트 1개 생성 및 저장 (DB에 1번 콘서트가 생김)
        Concert concert = Concert.builder()
                .concertTitle("가짜 콘서트")
                .image("https://picsum.photos/800/600")  //랜덤 이미지 생성 URL
                .concertTime(java.time.LocalTime.of(19, 0))
                .concertDate(java.time.LocalDate.now().plusDays(7))
                .concertPrice(150000) // 티켓 가격 15만 원
                .description("더미데이터 생성 중")
                .place("올드 트래포드")
                .reservationStartAt(java.time.LocalDateTime.now().minusDays(1)) // 어제부터 예매 시작 (예매 가능 상태 만들기)
                .reservationEndAt(java.time.LocalDateTime.now().plusDays(6)) // 공연 하루 전까지 예매 가능
                .status(com.example.mingpark.domain.ConcertStatus.ON_SALE) // 상태를 '판매 중'으로 세팅
                .build();

        concertRepository.save(concert);


        List<Seat> seats = new ArrayList<>();

        // 50개의 좌석 생성 처음 5개는 매진 나머지 5개는 임시점유 표시
        for (int i = 1; i <= 50; i++) {
            SeatStatus currentStatus = SeatStatus.AVAILABLE;
            if (i <= 5) {
                currentStatus = SeatStatus.RESERVED;
            } else if (i <= 10) {
                currentStatus = SeatStatus.HOLD;
            }
            Seat seat = Seat.builder()
                    .concert(concert)
                    .seatNumber(i)
                    .status(currentStatus) //
                    .build();

            seats.add(seat); // 리스트에 추가
        }


        seatRepository.saveAll(seats);
    }
}