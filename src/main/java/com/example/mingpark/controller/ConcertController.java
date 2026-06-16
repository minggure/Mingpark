package com.example.mingpark.controller;

import com.example.mingpark.dto.ConcertCreatRequestDto;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

@PostMapping("/api/concerts")
    public String createConcert(@RequestBody ConcertCreatRequestDto request){
        concertService.createConcert(request);
        return "공연 등록 완료";
    }

    @GetMapping("/api/concerts")
    public Page<ConcertResponseDto> getConcerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return concertService.getConcerts(page, size);
    }
}