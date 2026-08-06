import http from 'k6/http';
import { sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// "BEFORE" 시나리오: 대기열 게이트가 없다고 가정하고,
// 로그인만 하면 누구나 곧바로 좌석 선점(hold) + 예매 생성 API를 두드리는 상황을 재현한다.

const BASE_URL = 'http://localhost:8080';
const CONCERT_ID = 47;
const SEAT_ID_MIN = 2001;
const SEAT_ID_MAX = 2050;
const USER_POOL_SIZE = 300;
const TEST_PASSWORD = 'loadtest1234';

export const options = {
    scenarios: {
        direct_no_gate: {
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
    const userIndex = Math.floor(Math.random() * USER_POOL_SIZE);
    const loginId = `k6_loadtest_${userIndex}`;

    const loginRes = http.post(`${BASE_URL}/members/login`, JSON.stringify({
        loginId,
        password: TEST_PASSWORD,
    }), { headers: { 'Content-Type': 'application/json' } });

    if (loginRes.status !== 200) return;

    const params = {
        headers: { 'Content-Type': 'application/json' },
        cookies: loginRes.cookies,
    };

    // 대기열을 거치지 않고 곧바로 좌석 선점을 시도한다.
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

    sleep(1);
}
