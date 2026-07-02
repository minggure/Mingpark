package com.example.mingpark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WaitingStatusResponseDto {

    // 현재 유저의 상태: "WAIT"(대기 중) 또는 "ALLOWED"(내 차례! 진입 가능)
    private String status;

    // 내 앞에 대기 중인 사람 수 (0명이면 곧 입장한다는 뜻!)
    private Long rank;
}