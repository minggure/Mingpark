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

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Test
    void signup_success() {
        // given
        MemberSignupRequestDto request = createSignupRequest();
        when(memberRepository.existsByLoginId("mingpark")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        // when
        memberService.signup(request);

        // then
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

        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    private MemberSignupRequestDto createSignupRequest() {
        MemberSignupRequestDto request = new MemberSignupRequestDto();
        request.setName("Test Member");
        request.setLoginId("mingpark");
        request.setPassword("password123");
        request.setEmail("mingpark@example.com");
        return request;
    }
}
