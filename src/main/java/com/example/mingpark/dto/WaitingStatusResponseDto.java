package com.example.mingpark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대기열 상태 및 순위 정보를 전달하는 응답 DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WaitingStatusResponseDto {

    /** 대기 상태 (WAIT: 대기 중, ALLOWED: 진입 가능) */
    private String status;

    /** 대기열 내 실시간 순위 */
    private Long rank;
}