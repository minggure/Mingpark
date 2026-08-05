# 밍파크 (MingPark)

> **🎫 대규모 트래픽을 고려한 티켓팅 및 선착순 시스템 프로젝트**

인터파크 티켓 서비스를 벤치마킹하여, 대규모 트래픽 환경에서 발생할 수 있는 **동시성 문제**와 **서버 다운(Bottleneck)** 현상을 단계별로 해결해 나가는 백엔드 중심 챌린지 프로젝트입니다.

<br>

## 1. Technology Stack

| Language | Framework | DB / Cache | Auth |
| :---: | :---: | :---: | :---: |
| Java 17 | Spring Boot 4.0.6 (Web MVC, JPA, Validation, Thymeleaf) | MySQL, Redis | Spring Security (세션), 카카오 로그인 |

기타: Lombok, BCrypt(`spring-security-crypto`), Maven

<br>

## 2. Getting Started

### 사전 준비물
- JDK 17
- MySQL 서버
- Redis 서버 (로컬 기본값: `localhost:6379`)

### 실행 방법

```bash
# 1. 레포지토리 클론
git clone <repository-url>
cd Mingpark

# 2. 로컬 환경 설정 파일 생성 (git에 커밋되지 않음)
#    src/main/resources/application-local.properties
#    - 기본값(DB_URL/DB_USERNAME/DB_PASSWORD 미지정 시)은
#      jdbc:mysql://localhost:3306/mingpark_db, ming/ming 을 사용합니다.
#    - 필요 시 아래 값을 재정의하세요.

# 3. 애플리케이션 실행 (local 프로필)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

기본 프로필(`application.properties`)로 실행할 경우 아래 환경변수가 필요합니다.

| 환경변수 | 설명 | 필수 여부 |
| --- | --- | :---: |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | MySQL 접속 정보 | ✅ |
| `KAKAO_REST_API_KEY`, `KAKAO_CLIENT_SECRET` | 카카오 로그인 연동 키 | 카카오 로그인 사용 시 |
| `ADMIN_LOGIN_ID`, `ADMIN_PASSWORD`, `ADMIN_EMAIL` | 최초 기동 시 관리자 계정 자동 생성/동기화 | 관리자 기능 사용 시 |

서버 기동 후 `http://localhost:8080` 에서 정적 페이지(`index.html`, `login.html`, `register.html`, `mypage.html`)에 접근할 수 있습니다.

<br>

## 3. Project Structure

```
src/main/java/com/example/mingpark/
├── config/        # Security, Redis, Password, Web(정적 업로드 리소스), 관리자 계정 초기화
├── controller/     # REST/View 컨트롤러
├── domain/         # JPA 엔티티 (Member, Concert, Seat, Reservation, PaymentHistory ...)
├── dto/            # 요청/응답 DTO
├── event/          # 결제 성공/실패 애플리케이션 이벤트
├── exception/       # 도메인 예외
├── facade/         # Redis 분산 락 퍼사드 (좌석 임시 선점)
├── repository/     # Spring Data JPA 리포지토리
├── scheduler/      # 대기열 진입 허가 스케줄러
├── security/        # CustomUserDetails(Service)
└── service/         # 비즈니스 로직
```

<br>

## 4. 주요 기능

| 영역 | 내용 |
| --- | --- |
| 회원 | 회원가입/로그인(세션 기반), 카카오 소셜 로그인, `/members/me` 로그인 상태 조회 |
| 공연 관리 | 공연 등록/수정/삭제(관리자 전용), 페이징 목록/상세 조회, 포스터 이미지 업로드 |
| 좌석 & 예매 | 좌석 현황 조회, Redis 기반 좌석 임시 선점(5분 TTL), 예매 생성, 포인트 기반 결제/결제 실패 처리 |
| 대기열 | Redis Sorted Set 기반 대기열 진입/상태 조회/이탈, 1초 주기 스케줄러가 활성 인원(`MAX_ACTIVE_CAPACITY`)만큼 순차 입장 허가 |
| 마이페이지 | 예매 내역 목록/상세 조회, 예매 환불(공연 시작 전에 한해 포인트 복구) |

<br>

## 5. Project Roadmap

### ✅ Step 1. 인터파크 베이스 시스템
Controller-Service-Repository 구조, 회원/공연/좌석/예매 핵심 도메인 및 CRUD API — 구현 완료

### ✅ Step 2. 대기열 시스템 (트래픽 제어)
Redis ZSET(`queue:wait:concert:{id}`)로 대기 순번을 관리하고, 스케줄러가 주기적으로 활성 큐(`queue:active:concert:{id}`)로 인원을 이관 — 구현 완료

### 🔜 Step 3. 선착순 쿠폰 시스템 (분산 처리)
Kafka 기반 비동기 발급/결제 요청 처리 — 아직 구현되지 않음 (로드맵 예정)

<br>

## 6. 부하 테스트

- `src/test/java/load-test.js` — k6 스크립트 (로그인 후 좌석 예매 시나리오)
- `src/test/test.http` — 좌석 조회 API 수동 테스트

```bash
k6 run src/test/java/load-test.js
```

### 📊 대기열 게이트 적용 전/후 비교

대기열(`Step 2`)을 실제 좌석 선점 진입 관문으로 연결하기 전에는, 대기열 API(`/waiting/*`)와 좌석 선점 API(`/seats/{id}/hold`)가 서로 독립적으로 동작해 로그인만 하면 대기열을 거치지 않고도 바로 좌석 선점을 시도할 수 있었습니다. `SeatController.holdSeat`에 `WaitingService.isAllowed()` 검사를 추가해, **대기열을 통과(ALLOWED)한 사용자만 좌석을 선점할 수 있도록** 실제로 흐름을 연결한 뒤 동일한 조건(로컬 PC 1대, 공연 1개·좌석 50석, 계정 풀 300개)에서 k6로 전/후를 비교했습니다.

| 시나리오 | VUs / 시간 | 실행 스크립트 |
| --- | --- | :---: |
| **Before** — 게이트 없이 로그인 후 곧바로 `hold`+`reservation` 직접 호출 | 300 VU / 30s | `src/test/java/k6-direct-hold-test.js` |
| **After** — `waiting/join` → 상태 폴링 → `ALLOWED` 후에만 `hold`+`reservation` 호출 | 300 VU / 30s | `src/test/java/k6-waiting-test.js` |

| 지표 | Before (게이트 없음) | After (대기열 게이트) | 비고 |
| --- | ---: | ---: | --- |
| `hold` 요청 수 | 2,236건 | 1,211건 | 대기열이 유입을 조절 |
| `hold` 평균 응답시간 | 245.4 ms | **7.7 ms** | 약 32배 개선 |
| `hold` p95 응답시간 | 949.2 ms | **15.8 ms** | 약 60배 개선 |
| 전체 HTTP 평균 응답시간 | 960.9 ms | **118.6 ms** | 약 8배 개선 |
| 전체 HTTP p95 응답시간 | 3.78 s | **0.88 s** | |
| 전체 요청 실패율 | 65.3% (4,969/7,605) | 15.8% (1,553/9,820) | |
| 최종 예매 성공 건수 | 50건 (전 좌석 소진) | 50건 (전 좌석 소진) | 좌석 수는 동일하므로 결과는 같음 |
| 대기열 평균 대기시간 | – | 7.6 s (p95 20.4 s) | After만 발생하는 비용 |

**해석**: 좌석 총 개수가 50석으로 고정돼 있어 최종 예매 성공 건수는 두 경우 모두 동일합니다. 차이는 **그 50건을 처리하는 동안 백엔드(Redis/DB)가 견뎌야 했던 동시 요청의 형태**에 있습니다. 게이트가 없으면 300명이 동시에 `hold`를 두드려 순간적인 쓰기 경합과 지연이 크게 튀지만(p95 949ms), 대기열이 동시 활성 인원을 `MAX_ACTIVE_CAPACITY`(50명)로 제한하면 `hold` 요청이 소규모로 나뉘어 들어와 지연이 안정적으로 낮게 유지됩니다(p95 15.8ms). 그 대가로 사용자는 평균 7.6초의 대기 시간을 감수합니다 — 순간 부하를 대기 시간으로 변환하는 것이 이 기능의 핵심입니다.

> ⚠️ 로컬 PC 한 대에서 서버·MySQL·Redis·k6 클라이언트를 동시에 돌린 결과라 절대적인 수치는 프로덕션 환경과 다를 수 있습니다. 다만 동일한 하드웨어·동일한 좌석 수·동일한 계정 풀로 전/후를 비교했으므로 상대적인 개선 폭은 유효합니다.

<br>

## 7. Team Members (99즈)

| 전민규 (Team Leader) | 윤태형 | 김현정 |
| :---: | :---: | :---: |
| [@전민규(조장)](https://github.com/minggure) | [@윤태형](https://github.com/YunTaeng) | [@김현정](https://github.com/anthia-kim) |
| **Backend Lead** | **Backend / Infra** | **Backend / QA** |
| 프로젝트 총괄 및 도메인 설계, Redis 기반 티켓팅 대기열 구현, DB 성능 최적화 및 락 제어 | AWS & Docker 배포 환경 구축, Kafka 기반 선착순 쿠폰 분산 처리, CI/CD 파이프라인 자동화 | 핵심 도메인 비즈니스 로직 구현, Locust/k6 기반 부하 테스트 시나리오 작성, 병목 구간 분석 및 튜닝 |
