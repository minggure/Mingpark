package com.example.mingpark.repository;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local") // 테스트 실행 시 개인 로컬 DB 설정을 사용
@Transactional // 테스트가 끝나면 저장한 회원 데이터를 자동 롤백
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveAndFindByLoginId() {
        // given
        String loginId = "test-" + UUID.randomUUID();
        Member member = new Member(
                "Test Member",
                loginId,
                "password123",
                "test@example.com",
                MemberRole.USER
        );

        // when
        // 즉시 DB에 반영한 뒤 로그인 아이디 기반 조회 메서드를 검증
        Member savedMember = memberRepository.saveAndFlush(member);
        Optional<Member> foundMember = memberRepository.findByLoginId(loginId);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("Test Member");
        assertThat(foundMember.get().getPoint()).isEqualTo(1000000);
        assertThat(foundMember.get().getRole()).isEqualTo(MemberRole.USER);
        assertThat(memberRepository.existsByLoginId(loginId)).isTrue();
    }
}
