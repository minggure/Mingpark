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
// @RequiredArgsConstructor : Lombok이 생성자를 자동으로 만들어줌 (의존성 주입할 때 많이 씀)
// @AutoWired 보다는 @RequiredArgsConstructor 선호
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    // @Transactional 회원가입 작업을 하나의 트랜잭션으로 처리
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
    /**
     * 데이터베이스(DB) 및 암호화 검증을 통해 회원의 로그인 인증을 수행
     * 입력받은 로그인 아이디로 회원을 조회한 후, 회원이 존재하지 않거나
     * {@link PasswordEncoder}를 통한 암호화 비밀번호 검증이 실패하면 {@code null}을 반환
     * @param loginDto 클라이언트로부터 전달받은 로그인 요청 정보 (아이디, 평문 비밀번호)
     * @return 인증에 성공 시 {@link Member} 엔티티 객체, 인증 실패 시(아이디 부재 또는 비밀번호 불일치) {@code null}
     */
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
