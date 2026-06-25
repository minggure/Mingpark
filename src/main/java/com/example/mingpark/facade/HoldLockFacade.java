package com.example.mingpark.facade;


import com.example.mingpark.service.ConcertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoldLockFacade {
    private final RedisTemplate<String,String> redisTemplate;
    private final ConcertService concertService;

    public void holdSeat(Long seatId, Long memberId){
        String lockKey = "lock:seat:"  + ":" + seatId;

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, String.valueOf(memberId), 1, TimeUnit.MINUTES);

        if(Boolean.FALSE.equals(acquired)){
            log.warn("좌석 선점 실패 (이미 점유됨) -  seatId={}, requestUser={}", seatId, memberId);
            throw new IllegalArgumentException("이미 다른 분이 결제 중인 좌석입니다.");
        }
        try {
            concertService.processHoldSeat(seatId, memberId);
            log.info("좌석 임시 점유(5분) 성공  -  seatId={}, requestUser={}", seatId, memberId);
        } catch (Exception e){

            redisTemplate.delete(lockKey);
            log.error("DB 점유 처리 중 에러 밸상, Redis 락 즉시 해제  - seatId={}, requestUser={}", seatId, memberId);
            throw e;
        }

    }
}
