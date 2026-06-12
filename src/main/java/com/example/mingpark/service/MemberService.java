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

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(MemberSignupRequestDto request) {

        // 입력한 로그인 아이디가 이미 존재하는지 확인
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = new Member(
                request.getName(),
                request.getLoginId(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                MemberRole.USER
        );

        memberRepository.save(member);
    }
    public Member login(LoginRequestDto loginDto) {
        // DB에서 로그인 아이디로 회원 조회
        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginDto.getLoginId());

        // 아이디가 없으면 로그인 실패(null)
        if (findMemberOptional.isEmpty()) {
            return null;
        }

        Member member = findMemberOptional.get();

        // 비밀번호 검사 (틀리면 로그인 실패)
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            return null; // 비밀번호가 일치하지 않으면 바로 컷!
        }
        // 성공하면 회원 정보 반환
        return member;
    }
}
