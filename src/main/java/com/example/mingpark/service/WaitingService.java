package com.example.mingpark.service;

import com.example.mingpark.dto.WaitingStatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis Sorted Set 및 Set을 활용한 공연 예매 대기열 관리 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingService {

    private final StringRedisTemplate redisTemplate;

    private static final String WAIT_KEY_PREFIX = "queue:wait:concert:";
    private static final String ACTIVE_KEY_PREFIX = "queue:active:concert:";

    /**
     * 특정 공연의 대기열 진입 및 순위 조회 처리.
     *
     * @param concertId 공연 고유 식별 ID
     * @param memberId 회원의 고유 ID
     * @return 대기 상태 및 현재 순위 정보 DTO
     */
    public WaitingStatusResponseDto joinQueue(Long concertId, Long memberId) {
        String waitKey = WAIT_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;
        String memberIdStr = String.valueOf(memberId);

        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(activeKey, memberIdStr))) {
            return new WaitingStatusResponseDto("ALLOWED", 0L);
        }

        Double score = redisTemplate.opsForZSet().score(waitKey, memberIdStr);
        if (score == null) {
            long now = System.currentTimeMillis();
            redisTemplate.opsForZSet().add(waitKey, memberIdStr, now);
        }

        Long rank = redisTemplate.opsForZSet().rank(waitKey, memberIdStr);
        return new WaitingStatusResponseDto("WAIT", rank != null ? rank : 0L);
    }

    /**
     * 특정 공연의 대기열 현재 상태 및 실시간 대기 순위 조회.
     *
     * @param concertId 공연 고유 식별 ID
     * @param memberId 회원의 고유 ID
     * @return 진입 허가 또는 대기 순위 정보 DTO
     */
    public WaitingStatusResponseDto getStatus(Long concertId, Long memberId) {
        String waitKey = WAIT_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;
        String memberIdStr = String.valueOf(memberId);

        Boolean isActive = redisTemplate.opsForSet().isMember(activeKey, memberIdStr);
        if (Boolean.TRUE.equals(isActive)) {
            return new WaitingStatusResponseDto("ALLOWED", 0L);
        }

        Long rank = redisTemplate.opsForZSet().rank(waitKey, memberIdStr);

        if (rank == null) {
            return joinQueue(concertId, memberId);
        }

        return new WaitingStatusResponseDto("WAIT", rank);
    }

    /**
     * 대기열 순서에 따른 진입 허가 및 활성 큐 이관 처리.
     *
     * @param concertId 공연 고유 식별 ID
     * @param count 진입 허가 처리할 대상 인원수
     */
    public void allowEntry(Long concertId, int count) {
        String waitKey = WAIT_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;

        Set<String> membersToAllow = redisTemplate.opsForZSet().range(waitKey, 0, count - 1);

        if (membersToAllow == null || membersToAllow.isEmpty()) {
            return;
        }

        log.info("{}번콘서트 이번 타임 입장 : 진입 인원={}명", concertId, membersToAllow.size());

        for (String memberIdStr : membersToAllow) {
            redisTemplate.opsForSet().add(activeKey, memberIdStr);
            redisTemplate.opsForZSet().remove(waitKey, memberIdStr);
        }

        redisTemplate.expire(activeKey, 5, TimeUnit.MINUTES);
    }

    /**
     * 특정 회원의 대기열 및 활성 큐 점유 데이터 강제 삭제.
     *
     * @param concertId 공연 고유 식별 ID
     * @param memberId 회원의 고유 ID
     */
    public void clearUser(Long concertId, Long memberId) {
        redisTemplate.opsForZSet().remove(WAIT_KEY_PREFIX + concertId, String.valueOf(memberId));
        redisTemplate.opsForSet().remove(ACTIVE_KEY_PREFIX + concertId, String.valueOf(memberId));
    }
}