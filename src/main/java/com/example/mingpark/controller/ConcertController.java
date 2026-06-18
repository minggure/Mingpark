package com.example.mingpark.controller;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import com.example.mingpark.dto.ConcertCreatRequestDto;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.service.ConcertService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @PostMapping("/api/concerts")
    public ResponseEntity<String> createConcert(
            @RequestBody ConcertCreatRequestDto request,
            HttpServletRequest httpRequest
    ) {
        // 공연 등록 API에서 세션의 로그인 회원을 꺼내고, role이 ADMIN인지 확인
        if (!isAdmin(httpRequest)) {
            return ResponseEntity.status(403).body("관리자만 공연을 등록할 수 있습니다.");
        }

        concertService.createConcert(request);
        return ResponseEntity.ok("공연 등록 완료");
    }

    @GetMapping("/api/concerts")
    public Page<ConcertResponseDto> getConcerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return concertService.getConcerts(page, size);
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Member member)) {
            return false;
        }

        return member.getRole() == MemberRole.ADMIN;
    }
}
