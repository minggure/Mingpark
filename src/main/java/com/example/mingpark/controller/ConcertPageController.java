package com.example.mingpark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * JSON API를 담당하는 ConcertController와 달리 공연 HTML 화면을 반환한다.
 */
@Controller
@RequiredArgsConstructor
public class ConcertPageController {
    @GetMapping("/concerts/{concertId}")
    public String getConcertDetail(@PathVariable Long concertId) {
        // 예: /?target=14 주소로 보냄
        return "redirect:/?target=" + concertId;
    }
}
