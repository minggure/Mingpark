package com.example.mingpark.security;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/**
 * 인증된 사용자 정보 관리를 위한 UserDetails 구현체.
 */
public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String name;
    private final String loginId;
    private final String password;
    private final MemberRole role;
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

    public Long getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public MemberRole getRole() {
        return role;
    }
    /**
     * 사용자의 보유 권한 목록 반환 및 접두사(ROLE_) 매핑 처리.
     *
     * @param 사용자의 권한 컬렉션
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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}