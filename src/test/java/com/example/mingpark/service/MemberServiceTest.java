package com.example.mingpark.service;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import com.example.mingpark.dto.MemberSignupRequestDto;
import com.example.mingpark.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    // 실제 DB와 암호화 객체 대신 동작을 제어할 수 있는 가짜 객체 사용
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        // 각 테스트가 실행되기 전에 가짜 의존성을 주입한 서비스를 새로 생성
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Test
    void signup_success() {
        // given
        MemberSignupRequestDto request = createSignupRequest();
        // 중복되지 않은 아이디와 암호화 결과를 가정
        when(memberRepository.existsByLoginId("mingpark")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        // when
        memberService.signup(request);

        // then
        // Repository에 전달된 Member 객체를 가져와 저장 내용을 검증
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getName()).isEqualTo("Test Member");
        assertThat(savedMember.getLoginId()).isEqualTo("mingpark");
        assertThat(savedMember.getPassword()).isEqualTo("encoded-password");
        assertThat(savedMember.getEmail()).isEqualTo("mingpark@example.com");
        assertThat(savedMember.getRole()).isEqualTo(MemberRole.USER);
        assertThat(savedMember.getPoint()).isEqualTo(1000000);
    }

    @Test
    void signup_duplicateLoginId_throwsException() {
        // given
        MemberSignupRequestDto request = createSignupRequest();
        when(memberRepository.existsByLoginId("mingpark")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(IllegalArgumentException.class);

        // 중복 아이디라면 비밀번호 암호화와 회원 저장을 실행하지 않아야 함
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    // 여러 테스트에서 동일한 정상 회원가입 요청을 재사용
    private MemberSignupRequestDto createSignupRequest() {
        MemberSignupRequestDto request = new MemberSignupRequestDto();
        request.setName("Test Member");
        request.setLoginId("mingpark");
        request.setPassword("password123");
        request.setEmail("mingpark@example.com");
        return request;
    }
}
