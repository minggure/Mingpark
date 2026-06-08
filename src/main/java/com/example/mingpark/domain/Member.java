package com.example.mingpark.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    //회원가입 할 때 사용할 생성자 수정시 커밋메시지
    public Member(String name, String loginId, String password, String email, MemberRole role){
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.role = role;


    }
}
