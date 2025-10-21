package com.simplecoding.cheforest.jpa.admin.controller;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.security.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
// made by yes_ung
@RestController
public class PingController {

    private final ConcurrentHashMap<String, Long> activeUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionIdToUserId = new ConcurrentHashMap<>();
    private final Set<String> loggedInUserIds = ConcurrentHashMap.newKeySet();

    // sessionId -> Member 정보 맵핑
    private final ConcurrentHashMap<String, Member> sessionIdToMember = new ConcurrentHashMap<>();

    //    최대 동접자수
    private int peakConcurrentUsers = 0;
//     동접자수 기준 날짜
    private LocalDate peakDate = LocalDate.now();
//  누적 방문자 계산용
//   세션기준 중복카운트 제거용 set
    private Set<String> uniqueVisitorsToday = ConcurrentHashMap.newKeySet();
//    누적 단순 방문자수 저장
    private int totalVisitorsToday = 0;
//    누적 인증 방문자수 저장
    private int totalVisitMemberToday = 0;
//    누적 방문자 계산용 날짜
    private LocalDate visitorCountDate = LocalDate.now();


    @PostMapping("/ping")
    public void ping(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getSession(true).getId();

        // 동접자 날짜 기준 초기화
        LocalDate today = LocalDate.now();
        if (!today.equals(peakDate)) {
            peakDate = today;
            peakConcurrentUsers = 0;
        }
        //  누적 방문자 날짜 기준 초기화
        if (!today.equals(visitorCountDate)) {
            // 초기화
            visitorCountDate = today;
            totalVisitorsToday = 0;
            totalVisitMemberToday = 0;
            uniqueVisitorsToday.clear();
        }

        activeUsers.put(sessionId, System.currentTimeMillis());

        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String userId = principal.getName();
            // 세션ID -> 유저ID 맵핑
            sessionIdToUserId.put(sessionId, userId);
            // 유저ID -> 중복 제거된 로그인 유저 집합
            loggedInUserIds.add(userId);

            // 세션에서 CustomUserDetails → Member 꺼내기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                Member member = userDetails.getMember();
                sessionIdToMember.put(sessionId, member);  // 저장
            }

    // 누적 방문자 수 체크 (로그인 사용자 기준)
            if (uniqueVisitorsToday.add("LOGIN:" + userId)) {
                totalVisitorsToday++;
                totalVisitMemberToday++;
                System.out.println("👤 누적 로그인 방문자 수: " + totalVisitMemberToday);

            }

        } else {
            // 비로그인 사용자: visId 쿠키 체크
            String visId = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("visId".equals(c.getName())) {
                        visId = c.getValue();
                        break;
                    }
                }
            }

            // visId 없으면 새로 생성하고 방문자 수 증가
            if (visId == null) {
                visId = UUID.randomUUID().toString();

                // 오늘 자정까지 남은 시간 계산
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
                long secondsUntilMidnight = Duration.between(now, midnight).getSeconds();

                Cookie visCookie = new Cookie("visId", visId);
                visCookie.setPath("/");
                visCookie.setMaxAge((int) secondsUntilMidnight); // 오늘 자정까지
                visCookie.setHttpOnly(true);
                response.addCookie(visCookie);

                if (uniqueVisitorsToday.add("GUEST:" + visId)) {
                    totalVisitorsToday++;
                    System.out.println("👤 누적 비인증 방문자 수: " + totalVisitorsToday);
                }
            } else {
                // 쿠키가 있는데 처음 보는 visId면 여전히 추가
                if (uniqueVisitorsToday.add("GUEST:" + visId)) {
                    totalVisitorsToday++;
                    System.out.println("👤 누적 비인증 방문자 수 (재접속): " + totalVisitorsToday);
                }
            }

            sessionIdToUserId.remove(sessionId);
            sessionIdToMember.remove(sessionId); // 비로그인 처리 시 제거
        }
        // 최대 동접 갱신
        int currentActive = activeUsers.size();
        if (currentActive > peakConcurrentUsers) {
            peakConcurrentUsers = currentActive;
        }
    }








    @GetMapping("/admin/stats")
    public Map<String, Object> getStats() {
        long now = System.currentTimeMillis();
        long expiration = 3000;

        // 오래된 세션 제거
        activeUsers.entrySet().removeIf(entry -> now - entry.getValue() > expiration);

        // 세션 ID가 만료되면, 그에 매핑된 유저 ID도 체크해서 제거
        Set<String> expiredSessionIds = new HashSet<>();
        for (String sessionId : sessionIdToUserId.keySet()) {
            if (!activeUsers.containsKey(sessionId)) {
                expiredSessionIds.add(sessionId);
            }
        }

        // 세션->유저 맵핑 제거 & 유저 ID도 정리
        for (String expiredSessionId : expiredSessionIds) {
            String userId = sessionIdToUserId.remove(expiredSessionId);
            sessionIdToMember.remove(expiredSessionId); // ✅ 같이 제거

            // userId가 더 이상 어떤 세션에서도 사용되지 않으면 제거
            boolean stillActive = sessionIdToUserId.containsValue(userId);
            if (!stillActive) {
                loggedInUserIds.remove(userId);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("activeUsers", activeUsers.size());
        result.put("loggedInUsers", loggedInUserIds.size());
        result.put("peakConcurrentUsers", peakConcurrentUsers);
        result.put("peakDate", peakDate.toString());
        result.put("totalVisitorsToday", totalVisitorsToday);
        result.put("totalVisitMemberToday", totalVisitMemberToday);
        return result;
    }
    @GetMapping("/admin/loggedInUserList")
    public Map<String, Object> getLoggedInUserList() {
        long now = System.currentTimeMillis();
        long expiration = 3000;

        // activeUsers 갱신 (만료된 세션 제거)
        activeUsers.entrySet().removeIf(entry -> now - entry.getValue() > expiration);

        // 현재 접속 중인 로그인 사용자만 필터링
        List<Map<String, Object>> onlineLoggedInUsers = new ArrayList<>();
        for (Map.Entry<String, String> entry : sessionIdToUserId.entrySet()) {
            String sessionId = entry.getKey();

            if (activeUsers.containsKey(sessionId)) {
                Member member = sessionIdToMember.get(sessionId);
                if (member != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("loginId", member.getLoginId());
                    userInfo.put("nickname", member.getNickname());
                    userInfo.put("email", member.getEmail());
                    userInfo.put("role", member.getRole().name());
                    userInfo.put("point", member.getPoint());
                    userInfo.put("grade", member.getGrade());
                    userInfo.put("profile", member.getProfile());
                    userInfo.put("status", member.getSuspension());
                    userInfo.put("insertTime", member.getInsertTime());
                    userInfo.put("lastLoginTime", member.getLastLoginTime());
                    userInfo.put("memberIdx", member.getMemberIdx());
                    onlineLoggedInUsers.add(userInfo);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("loggedInUserList", onlineLoggedInUsers);
        return result;
    }




}

