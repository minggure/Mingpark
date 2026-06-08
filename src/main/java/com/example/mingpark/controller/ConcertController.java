package com.example.mingpark.controller;

import com.example.mingpark.dto.ConcertResponseDto;
import com.example.mingpark.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/api/concerts")
    public List<ConcertResponseDto> getConcerts() {
        return concertService.getAllConcerts();
    }
}