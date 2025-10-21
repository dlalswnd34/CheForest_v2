package com.simplecoding.cheforest.jpa.auth.security;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.auth.security.oauth.OAuth2UserInfo;
import com.simplecoding.cheforest.jpa.auth.security.oauth.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private String generateUniqueNickname(String baseNickname) {
        String nickname = baseNickname;
        int suffix = 1;

        while (memberRepository.existsByNickname(nickname)) {
            nickname = baseNickname + "_" + suffix++;
        }
        return nickname;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

        Member member = memberRepository.findBySocialIdAndProvider(userInfo.getId(), registrationId.toUpperCase())
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .socialId(userInfo.getId())
                                .provider(registrationId.toUpperCase())
                                .email(userInfo.getEmail() != null ? userInfo.getEmail() : "NO_EMAIL")
                                .nickname(generateUniqueNickname(userInfo.getName()))
                                .profile(userInfo.getImageUrl())
                                .role(Member.Role.USER)
                                // 기본값들
                                .password("SOCIAL_LOGIN")
                                .point(0L)
                                .grade("씨앗")
                                .build()
                ));

        attributes.put("provider", registrationId.toUpperCase());

        return new CustomOAuth2User(member, attributes);
    }

}
