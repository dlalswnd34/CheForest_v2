package com.simplecoding.cheforest.jpa.auth.security;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User, AuthUser {

    private final Member member;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    // JSP에서 principal.memberIdx 사용 가능
    public Long getMemberIdx() {
        return member != null ? member.getMemberIdx() : null;
    }

    // JSP에서 principal.nickname 사용 가능
    public String getNickname() {
        return member != null ? member.getNickname() : null;
    }

    // JSP에서 principal.grade 사용 가능
    public String getGrade() {
        return member != null ? member.getGrade() : null;
    }

    // JSP에서 principal.profile 사용 가능
    public String getProfile() {
        return member != null ? member.getProfile() : null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(member.getRole().name()));
    }

    @Override
    public String getName() {
        return member.getNickname(); // 화면에 기본 표시될 이름
    }

    @Override
    public Member getMember() {
        return member;
    }
}
