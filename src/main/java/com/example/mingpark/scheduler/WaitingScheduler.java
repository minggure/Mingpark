package com.example.mingpark.scheduler;

import com.example.mingpark.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Set;

/**
 * Redis 기반 예매 대기열을 주기적으로 스캔하여 활성 상태로 전환하는 스케줄러.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingScheduler {

    private final WaitingService waitingService;
    private final StringRedisTemplate redisTemplate;

    private static final String WAIT_KEY_PATTERN = "queue:wait:concert:*";
    private static final String WAIT_KEY_PREFIX = "queue:wait:concert:";
    private static final String ACTIVE_KEY_PREFIX = "queue:active:concert:";

    private static final int MAX_ACTIVE_CAPACITY = 50;

    /**
     * 주기적으로 전체 공연의 대기열 키를 조회하여 가용 정원만큼 대기자를 진입 허가 처리함.
     */
    @Scheduled(fixedDelay = 1000)
    public void processAllWaitingQueues() {
        Set<String> waitKeys = redisTemplate.keys(WAIT_KEY_PATTERN);
        if (waitKeys == null || waitKeys.isEmpty()) {
            return;
        }

        for (String key : waitKeys) {
            try {
                String concertIdStr = key.replace(WAIT_KEY_PREFIX, "");
                Long concertId = Long.parseLong(concertIdStr);

                String activeKey = ACTIVE_KEY_PREFIX + concertId;
                Long currentActiveCount = redisTemplate.opsForSet().size(activeKey);
                if (currentActiveCount == null) {
                    currentActiveCount = 0L;
                }

                int availableSlots = MAX_ACTIVE_CAPACITY - currentActiveCount.intValue();

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