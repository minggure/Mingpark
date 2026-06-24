package com.example.mingpark.security;

import com.example.mingpark.domain.Member;
import com.example.mingpark.domain.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String name;
    private final String loginId;
    private final String password;
    private final MemberRole role;

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