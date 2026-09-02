package com.example.mingpark.dto;

import com.example.mingpark.domain.Member;
import lombok.Getter;

/**
 * 마이페이지 메인 화면 노출용 사용자 자산 및 프로필 요약 응답 DTO.
 */
@Getter
public class MyPageMainResponseDto {

    private final String name;
    private final String email;
    private final int point;

    /**
     * Member 엔티티 원장을 기반으로 응답 DTO 객체를 생성한다.
     *
     * @param member 데이터 소스가 되는 회원 엔티티 객체
     */
    public MyPageMainResponseDto(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.point = member.getPoint();
    }
}