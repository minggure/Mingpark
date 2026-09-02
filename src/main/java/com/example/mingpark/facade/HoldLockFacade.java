package com.example.mingpark.facade;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * Redis를 이용한 TTL 구현 로직
 */
public class HoldLockFacade {
    private final StringRedisTemplate redisTemplate;

    public void holdSeat(Long seatId, Long memberId){
        String lockKey = "lock:seat:" + seatId;

        Boolean acquired = redisTemplate.opsForValue()
              .setIfAbsent(lockKey, String.valueOf(memberId), 5 , TimeUnit.MINUTES);


        if(Boolean.FALSE.equals(acquired)){
            log.warn("좌석 선점 실패 (이미 점유됨) -  seatId={}, requestUser={}", seatId, memberId);
            throw new IllegalArgumentException("이미 다른 분이 결제 중인 좌석입니다.");
        }

        log.info("좌석 임시 점유 성공 - seatId={} , requestUser={}", seatId, memberId);
        }

    /**
     * 결제 실패 or 결제 취소시 임시점유가 바로 해제되는 로직
     * @param seatId
     */
    public  void releaseHold(Long seatId){
        String lockKey = "lock:seat:" + seatId;
        log.info("좌석 임시 점유 해제 - seatId={}", seatId);
        redisTemplate.delete(lockKey);
    }

    }

