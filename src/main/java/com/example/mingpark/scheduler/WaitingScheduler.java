package com.example.mingpark.scheduler;

import com.example.mingpark.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingScheduler {

    private final WaitingService waitingService;
    private final StringRedisTemplate redisTemplate;

    private static final String WAIT_KEY_PATTERN = "queue:wait:concert:*";
    private static final String WAIT_KEY_PREFIX = "queue:wait:concert:";
    private static final String ACTIVE_KEY_PREFIX = "queue:active:concert:";

    // 🌟 [기획] 좌석 선택 창에 동시에 머무를 수 있는 최대 가상 정원 (예: 50명)
    private static final int MAX_ACTIVE_CAPACITY = 50;

    /**
     * 1초마다 대기열을 돌면서 '빈자리만큼만' 대기자를 활성화 창구로 밀어 넣음
     */
    @Scheduled(fixedDelay = 1000) // 이제 10초 기다릴 필요 없이 1초마다 빠르게 빈자리 스캔!
    public void processAllWaitingQueues() {
        Set<String> waitKeys = redisTemplate.keys(WAIT_KEY_PATTERN);
        if (waitKeys == null || waitKeys.isEmpty()) return;

        for (String key : waitKeys) {
            try {
                String concertIdStr = key.replace(WAIT_KEY_PREFIX, "");
                Long concertId = Long.parseLong(concertIdStr);

                // 1. 현재 해당 공연의 좌석 선택 창(Active Pool)에 몇 명이 있는지 레디스에서 카운트
                String activeKey = ACTIVE_KEY_PREFIX + concertId;
                Long currentActiveCount = redisTemplate.opsForSet().size(activeKey);
                if (currentActiveCount == null) currentActiveCount = 0L;

                // 2. 가용 가능한 빈자리 계산 (최대 정원 - 현재 인원)
                int availableSlots = MAX_ACTIVE_CAPACITY - currentActiveCount.intValue();

                // 3. 빈자리가 있을 때만, 딱 그 빈자리 수(availableSlots)만큼 대기열에서 유저를 채워 넣음!
                if (availableSlots > 0) {
                    log.info("🎵 공연ID [{}] 빈자리 발생! 현재 인원: {}명 -> {}명 더 모집",
                            concertId, currentActiveCount, availableSlots);
                    waitingService.allowEntry(concertId, availableSlots);
                }

            } catch (NumberFormatException e) {
                log.error("Key 파싱 에러: {}", key);
            }
        }
    }
}