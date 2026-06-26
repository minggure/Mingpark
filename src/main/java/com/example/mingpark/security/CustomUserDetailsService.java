package com.example.mingpark.security;

import com.example.mingpark.domain.Member;
import com.example.mingpark.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 데이터베이스 기반 사용자 인증 정보 조회 서비스.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 로그인 ID 기반 사용자 인증 정보 영속성 조회 및 UserDetails 변환 처리.
     *
     * @param loginId 조회 대상 로그인 ID
     * @return 인증 사용자 정보 객체(UserDetails) 반환
     * @throws UsernameNotFoundException 매칭되는 회원이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다. loginId=" + loginId));

        return new CustomUserDetails(member);
    }
}