package com.example.mingpark.config;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import com.example.mingpark.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 시스템 관리자 계정 초기화 및 동기화 컴포넌트.
 *
 * 환경변수에 관리자 정보가 설정되어 있으면:
 * 1. 해당 아이디의 회원이 이미 존재할 경우 관리자 권한과 비밀번호를 갱신한다.
 * 2. 존재하지 않을 경우 새로운 관리자 계정을 생성한다.
 */
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /** 관리자 인증용 고유 로그인 식별 ID (환경변수 주입, 누락 시 빈 문자열 처리로 예외 방지) */
    @Value("${ADMIN_LOGIN_ID:}")
    private String adminLoginId;

    /** 관리자 인증용 원시 비밀번호 (암호화 컴포넌트 연동 대상) */
    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    /** 시스템 관리자 연락처 및 알림용 이메일 주소 */
    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    /** 관리자 프로필 표시용 성명 (별도 지정 없을 시 기본값 '관리자'로 폴백) */
    @Value("${ADMIN_NAME:관리자}")
    private String adminName;

    /**
     * 애플리케이션 구동 후 관리자 데이터 영속화 처리.
     *
     * @param args 커맨드 라인 인자
     */
    @Override
    public void run(String... args) {

        // 관리자 계정 생성에 필요한 환경변수 중 하나라도 없으면 초기화를 진행x
        if (adminLoginId.isBlank() || adminPassword.isBlank() || adminEmail.isBlank()) {
            return;
        }

        // 동일한 로그인 아이디를 가진 회원이 이미 있는지 조회
        Member existingAdmin = memberRepository.findByLoginId(adminLoginId).orElse(null);

        // 기존 계정이 있다면 새 계정을 만들지 않고,
        // 관리자 권한과 암호화된 비밀번호만 갱신
        if (existingAdmin != null) {
            existingAdmin.changeRole(MemberRole.ADMIN);
            existingAdmin.changePassword(passwordEncoder.encode(adminPassword));
            memberRepository.save(existingAdmin);
            return;
        }

        // 기존 계정이 없으면 환경변수 값을 기반으로 관리자 계정을 새로 생성
        Member admin = Member.builder()
                .name(adminName)
                .loginId(adminLoginId)
                .password(passwordEncoder.encode(adminPassword)) // 비밀번호는 반드시 암호화해서 저장
                .email(adminEmail)
                .role(MemberRole.ADMIN)
                .build();

        memberRepository.save(admin);
    }
}