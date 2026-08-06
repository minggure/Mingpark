package com.example.mingpark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 공연 화면 컨트롤러
 * API:
 * GET /concerts/{concertId} - 특정 공연 상세 화면 요청 시 메인 화면으로 리다이렉트 처리
 */
@Controller
public class ConcertPageController {
    /**
     * [GET] 특정 공연 상세 뷰 요청을 수신하여 쿼리 스트링(target)을 포함한 메인 화면 경로로 리다이렉트 처리.
     *
     * @param concertId 타겟 공연의 고유 식별 ID
     * @return 메인 뷰 리다이렉트 경로 문자열 반환
     */
    @GetMapping("/concerts/{concertId}")
    public String getConcertDetail(@PathVariable Long concertId) {
        return "redirect:/?target=" + concertId;
    }
}
