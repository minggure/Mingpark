package com.example.mingpark.service;

import com.example.mingpark.dto.WaitingStatusResponseDto; // 윤탱이 DTO 명칭에 맞게 수정
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingService {

    private final StringRedisTemplate redisTemplate;

    private static final String WAIT_KEY_PREFIX = "queue:wait:concert:";
    private static final String ACTIVE_KEY_PREFIX = "queue:active:concert:";

    /**
     * 1. 대기열 진입
     */
    public WaitingStatusResponseDto joinQueue(Long concertId, Long memberId) {
        String waitKey = WAIT_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;
        String memberIdStr = String.valueOf(memberId);

        // 이미 Active(면제권)에 있으면 즉시 허가
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(activeKey, memberIdStr))) {
            return new WaitingStatusResponseDto("ALLOWED", 0L);
        }

        // Wait 큐에 진입 (Score는 현재 시간 타임스탬프)
        Double score = redisTemplate.opsForZSet().score(waitKey, memberIdStr);
        if (score == null) {
            long now = System.currentTimeMillis();
            redisTemplate.opsForZSet().add(waitKey, memberIdStr, now);
            //log.info("대기열 신규 진입 - concertId={}, memberId={}", concertId, memberId);
        }

        Long rank = redisTemplate.opsForZSet().rank(waitKey, memberIdStr);
        return new WaitingStatusResponseDto("WAIT", rank != null ? rank : 0L);
    }

    /**
     * 2. 대기 상태 조회
     */
    public WaitingStatusResponseDto getStatus(Long concertId, Long memberId) {
        String waitKey = WAIT_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;
        String memberIdStr = String.valueOf(memberId);

        // 🌟 오직 Active 큐에 존재하는 유저만 ALLOWED 판정을 받습니다!
        Boolean isActive = redisTemplate.opsForSet().isMember(activeKey, memberIdStr);
        if (Boolean.TRUE.equals(isActive)) {
            return new WaitingStatusResponseDto("ALLOWED", 0L);
        }

        // 여전히 대기 큐에 있다면 내 순위(Rank)를 반환합니다.
        Long rank = redisTemplate.opsForZSet().rank(waitKey, memberIdStr);

        // 만약 대기 큐에도 없고 Active에도 없으면 탈락된 것이므로 다시 join 시킵니다.
        if (rank == null) {
            return joinQueue(concertId, memberId);
        }

        // 🌟 rank가 0(맨 앞)이더라도, 스케줄러가 Active로 옮겨주기 전까지는 얌전히 "WAIT" 상태로 대기합니다!
        return new WaitingStatusResponseDto("WAIT", rank);
    }

    /**
     * 3. 스케줄러가 특정 공연의 대기열을 처리할 수 있도록 동적으로 변경
     */
    public void allowEntry(Long concertId, int count) {
        String waitKey = WAIT_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;

        // 대기열 맨 앞(Score 최저)에서 count만큼 유저를 뽑아옴
        java.util.Set<String> membersToAllow = redisTemplate.opsForZSet().range(waitKey, 0, count - 1);

        if (membersToAllow == null || membersToAllow.isEmpty()) {
            return;
        }

        log.info("{}번콘서트 이번 타임 입장 : 진입 인원={}명", concertId, membersToAllow.size());

        for (String memberIdStr : membersToAllow) {
            redisTemplate.opsForSet().add(activeKey, memberIdStr);
            redisTemplate.opsForZSet().remove(waitKey, memberIdStr);
        }

        // 10분 TTL 설정
        redisTemplate.expire(activeKey, 5, java.util.concurrent.TimeUnit.MINUTES);
    }

    public void clearUser(Long concertId, Long memberId) {
        redisTemplate.opsForZSet().remove(WAIT_KEY_PREFIX + concertId, String.valueOf(memberId));
        redisTemplate.opsForSet().remove(ACTIVE_KEY_PREFIX + concertId, String.valueOf(memberId));
    }
}