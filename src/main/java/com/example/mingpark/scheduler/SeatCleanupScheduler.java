//package com.example.mingpark.scheduler;
//
//import com.example.mingpark.domain.Seat;
//import com.example.mingpark.domain.SeatStatus;
//import com.example.mingpark.repository.SeatRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
///**
// * 만료 좌석 청소부 스케줄러
// * * Redis 분산 락 만료 기록을 MySQL 장부와 실시간 상호 대조하여
// * 미결제 상태로 5분이 경과한 좌석들만 AVAILABLE 상태로 일괄 수거 진행
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class SeatCleanupScheduler {
//
//    private final SeatRepository seatRepository;
//    private final RedisTemplate<String, String> redisTemplate;
//
//    /**
//     * 1분마다 주기적으로 가동하며 Redis에서 삭제되어 만료된 찌꺼기 HOLD 좌석 복구
//     */
//    @Scheduled(fixedDelay = 10000)//10000 -> 10초주기
//    @Transactional
//    public void cleanupExpiredSeats() {
//        List<Seat> holdSeats = seatRepository.findByStatus(SeatStatus.HOLD);
//
//        if (holdSeats.isEmpty()) {
//            return;
//        }
//
//        int releaseCount = 0;
//        for (Seat seat : holdSeats) {
//            // HoldLockFacade에서 사용 중인 레디스 락 키 규격을 매칭합니다.
//            String lockKey = "lock:seat::" + seat.getId();
//            Boolean hasKey = redisTemplate.hasKey(lockKey);
//
//            // Redis 장부에서 키가 지워져서 존재하지 않는다면(FALSE) 만료된 자원입니다.
//            if (Boolean.FALSE.equals(hasKey)) {
//                seat.changeStatus(SeatStatus.AVAILABLE);
//                releaseCount++;
//            }
//        }
//
//        if (releaseCount > 0) {
//            log.info("[SeatCleanupScheduler] 만료된 임시 선점 좌석 {}개 안전 수거 완료", releaseCount);
//        }
//    }
//}