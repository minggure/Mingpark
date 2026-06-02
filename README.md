# 밍파크
> **🎫 대규모 트래픽을 고려한 티켓팅 및 선착순 시스템 프로젝트**
> 
<br>

## 1. Project Overview (프로젝트 개요)
- **프로젝트명:** 밍파크
- **서비스 한 줄 소개:** 인터파크 티켓 서비스를 벤치마킹하여, 대규모 트래픽 환경에서 발생할 수 있는 **동시성 문제**와 **서버 다운(BottleNeck)** 현상을 단계별로 해결해 나가는 백엔드 중심의 챌린지 프로젝트입니다.

<br>

## 2. Technology Stack (기술 스택)

### 2.1 Frontend
| UI Framework | State Management | Build Tool | Package Manager |
| :---: | :---: | :---: | :---: |
| ![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=black) | ![Redux](https://img.shields.io/badge/Redux-764ABC?style=for-the-badge&logo=Redux&logoColor=white) | ![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=Vite&logoColor=white) | ![Yarn Berry](https://img.shields.io/badge/Yarn%20Berry-2C8EBB?style=for-the-badge&logo=Yarn&logoColor=white) |

### 2.2 Backend
| Language | Framework | Database |
| :---: | :---: | :---: |
| ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white) | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white) |

### 2.3 AI & Deploy & Cooperation
| Category | Tech Stack / Tools |
| :---: | :--- |
| **AI** | ![Python](https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=Python&logoColor=white) ![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=flat-square&logo=FastAPI&logoColor=white) |
| **Deploy** | ![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazon-aws&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white) |
| **Cooperation** | ![Jira](https://img.shields.io/badge/Jira-0052CC?style=flat-square&logo=Jira&logoColor=white) ![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=Notion&logoColor=white) ![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=Git&logoColor=white) |


<br>

## 3. Getting Started (실행 방법)

### 🏃‍♂️ FrontEnd
레포지토리를 클론한 후 아래 명령어를 순서대로 입력하여 프로젝트를 실행합니다.

```bash
# 1. 레포지토리 클론 및 폴더 이동
git clone [https://github.com/Duri-Salon/Duri-FE.git](https://github.com/Duri-Salon/Duri-FE.git)
cd Duri-FE

# 2. 의존성 설치 (yarn-berry 이용)
yarn install

# 3. 개발 서버 실행 (기본 주소: http://localhost:3000)
yarn dev

# 4. 프로덕션 빌드 및 미리보기 (dist 폴더 생성)
yarn build
yarn preview
```
## 4. Team Members (팀원 소개: 99즈)

저희는 **99즈** 팀입니다. 대용량 트래픽 챌린지를 해결하기 위해 다음과 같이 역할을 나누어 협업하고 있습니다.

| 전민규 (Team Leader) | 윤태형 | 김현정 |
| :---: | :---: | :---: |
| <img src="https://avatars.githubusercontent.com/u/204975717?v=4" width="130" height="130" style="border-radius: 50%;"/><br>[@전민규(조장)](https://github.com/minggure) | <img src="https://avatars.githubusercontent.com/u/52120957?v=4" width="130" height="130" style="border-radius: 50%;"/><br>[@윤태형](https://github.com/YunTaeng) | <img src="https://avatars.githubusercontent.com/u/156043679?v=4" width="130" height="130" style="border-radius: 50%;"/><br>[@김현정](https://github.com/anthia-kim) |
| **Backend Lead** | **Backend / Infra** | **Backend / QA** |
| - 프로젝트 총괄 및 도메인 설계<br>- Redis 기반 티켓팅 대기열 구현<br>- DB 성능 최적화 및 락 제어 | - AWS & Docker 배포 환경 구축<br>- Kafka 기반 선착순 쿠폰 분산 처리<br>- CI/CD 파이프라인 자동화 | - 핵심 도메인 비즈니스 로직 구현<br>- Locust 기반 부하 테스트 시나리오 작성<br>- 병목 구간 분석 및 튜닝 |

<br>

## 5. Project Roadmap (프로젝트 로드맵)

본 프로젝트는 가상의 대규모 트래픽을 발생시켜 서버의 한계를 확인하고, 이를 기술적으로 개선해 나가는 3단계 빌드업 과정으로 진행됩니다.

### 📌 Step 1. 인터파크 베이스 시스템 구축 (기본기)
- **목표:** 백엔드 레이어 구조(Controller-Service-Repository) 확립 및 핵심 도메인 설계
- **핵심 기능:** 공연 조회, 회차별 좌석 선택, 예매하기 (REST API)
- **집중 포인트:** JPA 연관 관계(공연-회차-좌석) 최적화 및 기본 CRUD 성능 확보

### 📌 Step 2. 아이돌 티켓팅 접속자 대기열 시스템 (트래픽 제어)
- **목표:** 수만 명의 동시 접속자가 밀려올 때 메인 DB와 서버가 다운되는 현상 방지
- **핵심 기술:** Redis Sorted Set (ZSET)
- **집중 포인트:** - 트래픽이 DB로 바로 인입되지 않도록 Redis 메모리 단에 유저를 시간 순으로 정렬 (순번 발급)
  - 주기적으로 가용자 수만큼 예매 권한(Token)을 이양하는 대기열 알고리즘 구현

### 📌 Step 3. 네고왕 이벤트 선착순 쿠폰 시스템 (분산 처리)
- **목표:** 대기열을 통과한 유저들이 한꺼번에 쿠폰 발급/결제를 요청할 때의 동시성 제어 및 병목 해결
- **핵심 기술:** Apache Kafka
- **집중 포인트:**
  - 주문/발급 요청을 Kafka 메시지 큐에 비동기로 적재하여 서버 부하 급증 방지
  - 백엔드 Worker(Consumer)가 순차적으로 이벤트를 처리하며 데이터 정합성(재고 초과 발급 방지) 유지

<br>

## 📊 부하 테스트 및 성능 개선 지표 (추후 기록 예정)
- **테스트 툴:** Locust
- 각 단계(Step) 완료 시점마다 가상 트래픽을 발생시켜 초당 처리량(TPS)과 응답 속도의 변화를 그래프와 수치로 기록할 예정입니다.
