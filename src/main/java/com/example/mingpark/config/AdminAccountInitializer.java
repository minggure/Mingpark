package com.example.mingpark.config;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import com.example.mingpark.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_LOGIN_ID:}")
    private String adminLoginId;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_NAME:관리자}")
    private String adminName;

    @Override
    public void run(String... args) {
        if (adminLoginId.isBlank() || adminPassword.isBlank() || adminEmail.isBlank()) {
            return;
        }

        Member existingAdmin = memberRepository.findByLoginId(adminLoginId).orElse(null);
        if (existingAdmin != null) {
            existingAdmin.changeRole(MemberRole.ADMIN);
            existingAdmin.changePassword(passwordEncoder.encode(adminPassword));
            memberRepository.save(existingAdmin);
            return;
        }

        Member admin = Member.builder()
                .name(adminName)
                .loginId(adminLoginId)
                .password(passwordEncoder.encode(adminPassword))
                .email(adminEmail)
                .role(MemberRole.ADMIN)
                .build();

        memberRepository.save(admin);
    }
}
