package com.simplecoding.cheforest.jpa.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id:}")
    private String kakaoClientId;

    // 우리 서비스 도메인 (카카오 redirect 등록 시 필요)
    @Value("${app.domain:http://localhost:8080}")
    private String appDomain;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {

        // 1) redirect 후보 결정
        String redirect = trimToNull(request.getParameter("redirect")); // 파라미터 우선
        if (redirect == null) {
            String referer = request.getHeader("Referer");
            if (referer != null && referer.startsWith(appDomain)) {
                // 도메인 부분 잘라내고 상대경로만 추출
                redirect = referer.substring(appDomain.length());
            }
        }
        if (!isSafeRedirect(redirect)) {
            redirect = "/";
        }

        // 2) 세션/쿠키 정리
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        deleteCookie("JSESSIONID", request, response);
        deleteCookie("remember-me", request, response);

        // 3) 로그 출력
        log.info("서비스 로그아웃 완료 → {}", redirect);

        // 4) 최종 이동
        response.sendRedirect(redirect);
    }

    // ----- Helper methods -----
    private static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    /** 상대 경로만 허용 */
    private static boolean isSafeRedirect(String url) {
        if (url == null) return false;
        if (url.startsWith("?") || url.startsWith("#")) return true;
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("//")) return false;
        return url.startsWith("/");
    }

    private static void deleteCookie(String name, HttpServletRequest req, HttpServletResponse res) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        res.addCookie(cookie);
    }
}
