package com.example.mingpark.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 정보 엔티티
 * 회원 기본 프로필, 로그인 계정 정보, 예매 포인트 및 권한 등급 관리.
 */
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동으로 숫자가 1올라가게 하는 AutoIncrement 설정

    @Column(name = "member_id") //여기서 Column은 ERD에서 정해둔 타입을 선언하는 부분
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int point = 1000000;

    @Column(nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Builder
    public Member(String name, String loginId, String password, String email, MemberRole role){
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.role = role;


    }

    /**
     * 회원의 권한 등급(Role)을 변경 처리.
     *
     * @param role 변경할 회원 권한 등급 값 (USER, ADMIN 등)
     */
    public void changeRole(MemberRole role) {
        this.role = role;
    }
    /**
     * 회원의 비밀번호를 변경 처리.
     *
     * @param password 변경할 암호화된 새 비밀번호 문자열
     */
    public void changePassword(String password) {
        this.password = password;
    }
}
