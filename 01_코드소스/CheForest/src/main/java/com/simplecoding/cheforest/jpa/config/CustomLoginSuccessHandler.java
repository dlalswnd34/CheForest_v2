package com.simplecoding.cheforest.jpa.config;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.auth.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();
        Member member = null;
        String loginId = null;

        // 1) 사용자 식별
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            loginId = userDetails.getUsername();
            member = memberRepository.findByLoginId(loginId).orElse(null);
            request.getSession().setAttribute("provider", "LOCAL");
        } else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
            String provider = null;
            if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oAuth2Token) {
                provider = oAuth2Token.getAuthorizedClientRegistrationId();
            }
            if (provider != null) provider = provider.toUpperCase();

            String socialId = extractSocialId(provider, oAuth2User.getAttributes());
            if (socialId != null && provider != null) {
                member = memberRepository.findBySocialIdAndProvider(socialId, provider).orElse(null);
                request.getSession().setAttribute("provider", provider);
            }
        }

        if (loginId != null) {
            memberService.updateLastLoginTime(loginId);
            log.info("마지막 로그인 시간 업데이트 완료: {}", loginId);
        }

        if (member != null && member.getNickname() != null && member.getNickname().contains("_")) {
            String originalNickname = member.getNickname().split("_")[0];
            request.getSession().setAttribute("originalNickname", originalNickname);
            log.info("자동 생성 닉네임 감지 → 모달 준비 (원래: {}, 현재: {})", originalNickname, member.getNickname());
        }

        // 2) redirect 파라미터/쿠키 수집
        String redirect = trimToNull(request.getParameter("redirect"));
        if (redirect == null) {
            redirect = trimToNull(java.net.URLDecoder.decode(readCookie(request, "post_login_redirect"), StandardCharsets.UTF_8));
        }

        // 2-1) 안전 필터링
        if (redirect != null && (
                redirect.startsWith("/auth/register") ||
                        redirect.startsWith("/auth/find-id") ||
                        redirect.startsWith("/auth/find-password") ||
                        redirect.startsWith("/auth/login")
        )) {
            redirect = null; // 인증 관련 페이지로는 보내지 않음
        }
        if (redirect != null && !isSafeRedirect(redirect)) {
            log.warn("차단된 redirect 요청: {}", redirect);
            redirect = null;
        }

        // ✅ 항상 로그인 성공 시 쿠키/실패정보 정리
        deleteCookie("post_login_redirect", request, response);
        clearAuthenticationAttributes(request);

        if (redirect != null) {
            // SavedRequest가 있으면 우선되어 버리므로 제거하고 우리가 지정한 경로로 보냄
            RequestCache rc = new HttpSessionRequestCache();
            rc.removeRequest(request, response);

            setUseReferer(false);        // Referer 사용 안 함 (auth 페이지로 튀는 것 방지)
            setDefaultTargetUrl(redirect);
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 3) SavedRequest → 없으면 "/" 로
        setUseReferer(false);            // Referer 미사용 권장
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }

    @SuppressWarnings("unchecked")
    private String extractSocialId(String provider, java.util.Map<String, Object> attrs) {
        if (provider == null || attrs == null) return null;
        switch (provider) {
            case "KAKAO":
                return attrs.get("id") != null ? String.valueOf(attrs.get("id")) : null;
            case "GOOGLE":
                Object sub = attrs.get("sub");
                return sub != null ? String.valueOf(sub) : null;
            case "NAVER":
                Object resp = attrs.get("response");
                if (resp instanceof java.util.Map<?, ?> m) {
                    Object naverId = ((java.util.Map<String, Object>) m).get("id");
                    return naverId != null ? String.valueOf(naverId) : null;
                }
                Object id = attrs.get("id");
                return id != null ? String.valueOf(id) : null;
            default:
                return null;
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
    private static boolean isSafeRedirect(String url) {
        if (url == null) return false;
        if (url.startsWith("?") || url.startsWith("#")) return true;
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("//")) return false;
        return url.startsWith("/");
    }
    private static String readCookie(HttpServletRequest req, String name) {
        var cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) if (name.equals(c.getName())) return c.getValue();
        return null;
    }
    private static void deleteCookie(String name, HttpServletRequest req, HttpServletResponse res) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        res.addCookie(cookie);
    }
}
