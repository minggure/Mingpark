package com.example.mingpark.repository;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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
