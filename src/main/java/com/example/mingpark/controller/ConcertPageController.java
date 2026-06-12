package com.example.mingpark.controller;

import com.example.mingpark.dto.ConcertDetailResponseDto;
import com.example.mingpark.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * JSON API를 담당하는 ConcertController와 달리 공연 HTML 화면을 반환한다.
 */
@Controller
@RequiredArgsConstructor
public class ConcertPageController {

    private final ConcertService concertService;

    // URL의 공연 ID로 상세 정보를 조회해 Thymeleaf 템플릿에 전달한다.
    @GetMapping("/concerts/{concertId}")
    public String getConcertDetail(@PathVariable Long concertId, Model model) {
        ConcertDetailResponseDto concert = concertService.getConcertDetail(concertId);
        model.addAttribute("concert", concert);

        return "concert-detail";
    }
}
