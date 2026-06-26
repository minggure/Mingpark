package com.example.mingpark.service;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import com.example.mingpark.dto.LoginRequestDto;
import com.example.mingpark.dto.MemberSignupRequestDto;
import com.example.mingpark.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
/**
 * 회원 가입 및 계정 정보 처리 비즈니스 로직 서비스.
 */
@Service
@RequiredArgsConstructor
// @RequiredArgsConstructor : Lombok이 생성자를 자동으로 만들어줌 (의존성 주입할 때 많이 씀)
// @AutoWired 보다는 @RequiredArgsConstructor 선호
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    // @Transactional 회원가입 작업을 하나의 트랜잭션으로 처리
    /**
     * 로그인 ID 중복 검증 및 패스워드 암호화를 거쳐 신규 회원 정보 영속화 처리.
     *
     * @param request 가입할 회원 상세 정보 DTO
     * @throws IllegalArgumentException 이미 존재하는 로그인 ID일 경우 발생
     */
    public void signup(MemberSignupRequestDto request) {

        // 입력한 로그인 아이디가 이미 존재하는지 확인
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = Member.builder()
                .name(request.getName())
                .loginId(request.getLoginId())
                // 비밀번호 암호화
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(MemberRole.USER)
                .build();

        memberRepository.save(member);
    }
}