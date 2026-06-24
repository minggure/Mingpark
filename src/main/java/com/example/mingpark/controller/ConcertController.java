package com.example.mingpark.controller;

import com.example.mingpark.domain.MemberRole;
import com.example.mingpark.dto.ConcertCreatRequestDto;
import com.example.mingpark.dto.ConcertDetailResponseDto;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.security.CustomUserDetails; // 추가된 시큐리티 디테일 클래스 임포트
import com.example.mingpark.service.ConcertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 공연 컨트롤러
 * API:
 * POST /api/concerts              - 관리자 공연 정보 시스템 등록 (FN-BK-01)
 * GET  /api/concerts              - 메인 화면 현재 상영 및 예정 공연 목록 페이징 조회 (FN-BK-02)
 * GET  /api/concerts/{concertId} - 특정 공연 단일 상세 정보 및 티켓 상태 종합 조회 (FN-BK-03)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;
    /**
     * [POST] 관리자 화면에서 입력한 새로운 공연 정보를 시스템에 등록합니다.
     * * @param request 등록할 공연 제목, 장소, 시간, 티켓 가격, 포스터 이미지 경로 등이 담긴 DTO
     *
     * @return 공연 등록 성공 여부를 알리는 안내 문자열 ("공연 등록 완료")
     */
    @PostMapping("/api/concerts")
    public ResponseEntity<String> createConcert(
            @RequestBody ConcertCreatRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || userDetails.getRole() != MemberRole.ADMIN) {
            return ResponseEntity.status(403).body("관리자만 공연을 등록할 수 있습니다.");
        }
        concertService.createConcert(request);
        return ResponseEntity.ok("공연 등록 완료");
    }

    /**
     * [GET] 메인 화면에 노출할 현재 상영 및 예정 공연 목록을 페이징 처리하여 조회합니다.
     * 기본적으로 한 페이지에 6개씩 끊어서 카드 형식의 레이아웃 데이터로 제공합니다.
     * * @param page 조회할 페이지 번호 (0부터 시작, 기본값: 0)
     *
     * @param size 한 페이지에 포함할 공연 리스트 개수 (기본값: 6)
     * @return 페이징 정보(현재 페이지, 전체 페이지 등)와 공연 목록 DTO(Page 객체) 반환
     */
    @GetMapping("/api/concerts")
    public Page<ConcertResponseDto> getConcerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return concertService.getConcerts(page, size);
    }

    /**
     * [GET] 특정 공연의 단일 상세 정보와 티켓 상태 등을 종합적으로 조회.
     * * @param concertId 상세 조회할 공연의 고유 식별 고유 ID
     *
     * @return 200 OK 응답과 함께 해당 공연의 단일 상세 정보 DTO 반환
     */
    @GetMapping("/api/concerts/{concertId}")
    public ConcertDetailResponseDto getConcertDetailApi(@PathVariable Long concertId) {
        return concertService.getConcertDetail(concertId);
    }
}