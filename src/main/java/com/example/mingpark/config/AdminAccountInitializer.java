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
 * 애플리케이션 실행 시 관리자 계정을 초기화하는 클래스.
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

    // 환경변수 ADMIN_LOGIN_ID 값을 관리자 로그인 아이디로 주입
    // 값이 없으면 빈 문자열("")을 기본값으로 사용
    @Value("${ADMIN_LOGIN_ID:}")
    private String adminLoginId;

    // 환경변수 ADMIN_PASSWORD 값을 관리자 비밀번호로 주입
    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    // 관리자 이메일로 주입
    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    // 관리자 이름은 별도 설정이 없으면 "관리자"를 기본값으로 사용
    @Value("${ADMIN_NAME:관리자}")
    private String adminName;

    /**
     * Spring Boot 실행이 완료된 직후 자동으로 실행
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