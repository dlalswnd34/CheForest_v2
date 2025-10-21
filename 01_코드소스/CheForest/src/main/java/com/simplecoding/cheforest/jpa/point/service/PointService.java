package com.simplecoding.cheforest.jpa.point.service;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.point.entity.PointHistory;
import com.simplecoding.cheforest.jpa.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    // 제한 포함된 포인트 적립
    public void addPointWithLimit(Member member, String actionType) {
        // 1) 오늘 총 포인트
        Long todayTotal = pointHistoryRepository.sumTodayPoints(member.getMemberIdx());
        if (todayTotal >= 500) {
            return; // 하루 500점 제한
        }

        // 2) 오늘 해당 액션 횟수
        Long todayActionCount = pointHistoryRepository.countTodayActions(member.getMemberIdx(), actionType);

        if ("POST".equals(actionType)) {
            if (todayActionCount >= 3) return; // 글 3개 제한
            addPoint(member, "POST", 100L);
        } else if ("COMMENT".equals(actionType)) {
            if (todayActionCount >= 20) return; // 댓글 20개 제한
            addPoint(member, "COMMENT", 10L);
        }
    }

    // 순수 포인트 적립 (재사용용)
    public void addPoint(Member member, String actionType, Long point) {
        // 1) 포인트 이력 저장
        PointHistory history = new PointHistory();
        history.setMember(member);
        history.setActionType(actionType);
        history.setPoint(point);
        pointHistoryRepository.save(history);

        // 2) 누적 포인트 갱신
        Long newPoint = member.getPoint() + point;
        member.setPoint(newPoint);

        // 3) 등급 자동 계산
        member.setGrade(calculateGrade(newPoint));

        memberRepository.save(member);
    }

    // 등급 계산
    private String calculateGrade(Long point) {
        if (point == null) return "씨앗";
        if (point < 1000) return "씨앗";
        else if (point < 2000) return "뿌리";
        else if (point < 3000) return "새싹";
        else if (point < 4000) return "나무";
        else return "숲";
    }

    // 오늘 포인트 합산
    @Transactional(readOnly = true)
    public Long getTodayPoints(Long memberId) {
        Long todayPoints = pointHistoryRepository.sumTodayPoints(memberId);
        if (todayPoints == null) {
            todayPoints = 0L;
        }
        System.out.println("오늘 포인트 합계: " + todayPoints); // 디버깅용
        return todayPoints;
    }

    // 이번 주 포인트 합산
    @Transactional(readOnly = true)
    public Long getWeekPoints(Long memberId) {
        LocalDate today = LocalDate.now();
        LocalDateTime weekStart = today.with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime weekEnd = today.with(java.time.DayOfWeek.SUNDAY).atTime(LocalTime.MAX);
        return pointHistoryRepository.sumPointsInPeriod(memberId, weekStart, weekEnd);
    }

    // 다음 등급까지 남은 점수
    @Transactional(readOnly = true)
    public Long getNextGradePoint(Long currentPoint) {
        if (currentPoint == null) return 1000L;
        if (currentPoint >= 4000) return 0L;
        long remainder = currentPoint % 1000;
        return 1000 - remainder;
    }
}
