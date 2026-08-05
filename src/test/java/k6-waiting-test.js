import http from 'k6/http';
import { sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// "AFTER" 시나리오: 대기열(Redis ZSET)을 통과(ALLOWED)한 사용자만
// 좌석 선점(hold) + 예매 생성 API에 도달할 수 있다.

const BASE_URL = 'http://localhost:8080';
const CONCERT_ID = 47; // 누오 그려진 곳
const SEAT_ID_MIN = 2001;
const SEAT_ID_MAX = 2050;
const USER_POOL_SIZE = 300; // 미리 만들어 둘 테스트 계정 수 (setup()에서 1회 생성)
const TEST_PASSWORD = 'loadtest1234';

export const options = {
    scenarios: {
        queue_gated: {
            executor: 'constant-vus',
            vus: 300,
            duration: '30s',
        },
    },
};

export const holdAttempts = new Counter('hold_attempts');
export const holdSuccess = new Counter('hold_success');
export const holdDuration = new Trend('hold_duration_ms');
export const reservationAttempts = new Counter('reservation_attempts');
export const reservationSuccess = new Counter('reservation_success');
export const queueWaitTime = new Trend('queue_wait_time_ms');

// 대기열 API가 실제 로그인 세션(@AuthenticationPrincipal)을 요구하므로
// 부하 테스트 시작 전에 로그인 가능한 가상 유저 계정을 미리 만들어 둔다.
// setup()은 k6에서 한 번만(순차) 실행되며, 이미 존재하는 계정은
// signup이 400(duplicate)을 반환해도 무시하고 계속 진행한다 (재실행 가능).
export function setup() {
    for (let i = 0; i < USER_POOL_SIZE; i++) {
        http.post(`${BASE_URL}/members/signup`, JSON.stringify({
            name: `k6user${i}`,
            loginId: `k6_loadtest_${i}`,
            password: TEST_PASSWORD,
            email: `k6_loadtest_${i}@loadtest.local`,
        }), { headers: { 'Content-Type': 'application/json' } });
    }
}

export default function () {
    // 🌟 매 반복마다 미리 만들어 둔 계정 풀에서 무작위로 하나를 골라
    // 실제로 로그인해서 세션 쿠키를 발급받는다 (스푸핑 쿠키 사용 불가).
    const userIndex = Math.floor(Math.random() * USER_POOL_SIZE);
    const loginId = `k6_loadtest_${userIndex}`;

    const loginRes = http.post(`${BASE_URL}/members/login`, JSON.stringify({
        loginId,
        password: TEST_PASSWORD,
    }), { headers: { 'Content-Type': 'application/json' } });

    if (loginRes.status !== 200) return; // 로그인 실패 시 이번 반복은 스킵

    const params = {
        headers: { 'Content-Type': 'application/json' },
        cookies: loginRes.cookies,
    };

    const joinUrl = `${BASE_URL}/api/concerts/${CONCERT_ID}/waiting/join`;
    const statusUrl = `${BASE_URL}/api/concerts/${CONCERT_ID}/waiting/status`;
    const leaveUrl = `${BASE_URL}/api/concerts/${CONCERT_ID}/waiting/leave`;

    const queueEnteredAt = Date.now();

    // 1️⃣ 대기열 최초 등록 슛!
    let res = http.post(joinUrl, JSON.stringify({}), params);
    if (res.status !== 200) return; // 서버가 뻗으면 가드문 작동

    let jsonBody = JSON.parse(res.body);

    // 2️⃣ 대기열 내부 폴링 (현실적인 주기로 네트워크 소켓 고갈 방지)
    while (jsonBody.status === 'WAIT') {
        sleep(2);
        res = http.get(statusUrl, params);
        if (res.status !== 200) break;
        jsonBody = JSON.parse(res.body);
    }

    // 3️⃣ 🎉 대기열 통과 (Active Pool 진입) -> 실제 좌석 선점/예매 시도
    if (jsonBody.status === 'ALLOWED') {
        queueWaitTime.add(Date.now() - queueEnteredAt);

        const seatId = SEAT_ID_MIN + Math.floor(Math.random() * (SEAT_ID_MAX - SEAT_ID_MIN + 1));
        const holdUrl = `${BASE_URL}/api/concerts/${CONCERT_ID}/seats/${seatId}/hold`;

        holdAttempts.add(1);
        const holdRes = http.post(holdUrl, null, params);
        holdDuration.add(holdRes.timings.duration);

        if (holdRes.status === 200) {
            holdSuccess.add(1);

            const reservationUrl = `${BASE_URL}/api/concerts/${CONCERT_ID}/seats/${seatId}/reservations`;
            reservationAttempts.add(1);
            const reservationRes = http.post(reservationUrl, null, params);
            if (reservationRes.status === 200) {
                reservationSuccess.add(1);
            }
        }

        // 4️⃣ 🔓 볼일 다 봤으니 퇴장 API 호출 -> 레디스 빈자리 1개 반납하고 다음 주자에게 바톤 토스!
        http.post(leaveUrl, JSON.stringify({}), params);
    }
}
