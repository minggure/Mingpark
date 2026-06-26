package com.example.mingpark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis를 활용하여 좌석의 임시 선점 및 해제하는 서비스
 */
@Service
@RequiredArgsConstructor
public class SeatReservationService {

    // RedisConfig에서 등록한 RedisTemplate 빈이 여기에 자동으로 주입
    private final RedisTemplate<String, String> redisTemplate;

    // Redis 내에서 티켓 예매용 좌석 키임을 식별하기 위한 접두사
    private static final String SEAT_KEY_PREFIX = "seat::";

    /**
     * 특정 좌석에 대해 5분간 임시 점유
     *
     * @param concertId 공연 고유 식별 ID
     * @param seatNumber 선택한 좌석 번호
     * @param memberId 회원의 고유 ID
     * @return 선점 성공 시 true, 다른 유저가 선점 중이면 false
     */
    public boolean occupySeat(Long concertId, int seatNumber, Long memberId) {
        String redisKey = SEAT_KEY_PREFIX + concertId + "::" + seatNumber;

        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, String.valueOf(memberId));

        if (Boolean.TRUE.equals(success)) {
            redisTemplate.expire(redisKey, 10, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }

    /**
     * 현재 특정 좌석을 누가 선점하고 있는지 조회.
     *
     * @param concertId 공연 고유 식별 ID
     * @param seatNumber 선택한 좌석 번호
     * @return 점유 중인 회원의 고유 ID 문자열, 선점된 내역이 없거나 만료되었다면 null 반환
     */
    public String getSeatOccupant(Long concertId, int seatNumber) {
        String redisKey = SEAT_KEY_PREFIX + concertId + "::" + seatNumber;
        return redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 유저가 결제를 완료하여 진짜 예매를 성공했거나, 명시적으로 취소했을 때 선점 데이터를 강제 삭제.
     *
     * @param concertId 공연 고유 식별 ID
     * @param seatNumber 선택한 좌석 번호
     */
    public void releaseSeat(Long concertId, int seatNumber) {
        String redisKey = SEAT_KEY_PREFIX + concertId + "::" + seatNumber;
        redisTemplate.delete(redisKey);
    }
}