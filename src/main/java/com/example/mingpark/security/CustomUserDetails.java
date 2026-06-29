package com.example.mingpark.security;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/**
 * 인증된 사용자 정보 관리를 위한 UserDetails 구현체.
 */
public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long memberId;
    @Getter
    private final MemberRole role;
    @Getter
    private final String name;
    private final String loginId;
    private final String password;


    /**
     * Member 엔티티 데이터를 시큐리티 인증 규격으로 변환 처리.
     *
     * @param member 회원 엔티티 객체
     */
    public CustomUserDetails(Member member) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.role = member.getRole();
    }

    /**
     * 사용자의 권한을 Spring Security 권한 형식으로 반환한다.
     *
     * @return ROLE_ 접두사가 붙은 권한 컬렉션
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override // 계정 만료 안 됨
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정 잠금 아님
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 비밀번호 만료 안 됨
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정 활성화 상태
    public boolean isEnabled() {
        return true;
    }

}
