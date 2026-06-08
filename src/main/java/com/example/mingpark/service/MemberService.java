package com.example.mingpark.service;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import com.example.mingpark.dto.MemberSignupRequest;
import com.example.mingpark.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void signup(MemberSignupRequest request) {
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = new Member(
                request.getName(),
                request.getLoginId(),
                request.getPassword(),
                request.getEmail(),
                MemberRole.USER
        );

        memberRepository.save(member);
    }
}