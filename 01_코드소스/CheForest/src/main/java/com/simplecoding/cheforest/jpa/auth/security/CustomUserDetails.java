package com.simplecoding.cheforest.jpa.auth.security;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails, AuthUser, Serializable {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    // 맴버값 가지고오기
    @Override
    public Member getMember() {
        return member;
    }

    // PK 꺼내는 메서드 추가
    public Long getMemberIdx() {
        return member.getMemberIdx();
    }

    public String getNickname() {   // ✅ JSP에서 principal.nickname
        return member.getNickname();
    }

    public String getGrade() {      // ✅ JSP에서 principal.grade
        return member.getGrade();
    }

    public String getProfile() {  // ✅ JSP에서 principal.profileImage
        return member.getProfile();
    }

    public String getEmail() {
        return member.getEmail();
    }

//   권한 정보는 멤버엔티티에 ROLE 필드에서 가지고 옴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole().name()));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    // 아래 4개는 계정 상태 제어 (true 고정으로 두면 기본 활성)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
