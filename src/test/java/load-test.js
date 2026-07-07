import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 10,
    duration: '100s',
};

// 테스트 함수 내부에서 로그인 처리를 하면 각 가상 유저가 각각 로그인하게 됩니다.
export default function () {
    const loginUrl = 'http://localhost:8080/members/login'; // 본인 프로젝트 로그인 주소
    const reservationUrl = 'http://localhost:8080/api/concerts/1/seats/1/reservations';

    // 1. 로그인 시도
    const loginRes = http.post(loginUrl, JSON.stringify({
        loginId: 'admin', // username 대신 loginId로 수정
        password: 'admin1234'
    }), { headers: { 'Content-Type': 'application/json' } });

    // 2. 로그인 성공 시에만 예약 시도
    if (loginRes.status === 200) {
        const params = {
            headers: { 'Content-Type': 'application/json' },
            cookies: loginRes.cookies // 로그인 후 받은 쿠키를 그대로 사용
        };

        const res = http.post(reservationUrl, null, params);

        check(res, {
            'status is 200': (r) => r.status === 200,
            'status is 400': (r) => r.status === 400,
        });
    } else {
        console.log(`로그인 실패! 상태 코드: ${loginRes.status}`);
    }
}