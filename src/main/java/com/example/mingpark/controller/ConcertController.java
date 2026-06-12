package com.example.mingpark.controller;

import com.example.mingpark.dto.ConcertCreatRequesstDto;
import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ConcertController {

    private final ConcertService concertService;

//    @GetMapping("/api/concerts")
//    public List<ConcertResponseDto> getConcerts() {return concertService.getAllConcerts();
//    }
    @PostMapping("/api/concerts")
    public String createConcert(@RequestBody ConcertCreatRequesstDto request){
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