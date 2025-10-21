package com.simplecoding.cheforest.jpa.point.repository;

import com.simplecoding.cheforest.jpa.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // 1. 오늘 획득 포인트 합산, 결과 없을경우 0
    @Query(value = "SELECT NVL(SUM(p.POINT), 0) " +
            "FROM POINT_HISTORY p " +
            "WHERE p.MEMBER_IDX = :memberIdx " +
            "AND TRUNC(p.INSERT_TIME) = TRUNC(SYSDATE)",
            nativeQuery = true)
    Long sumTodayPoints(@Param("memberIdx") Long memberIdx);

    // 2. 특정 기간 동안 획득 포인트 합산 (주간/월간)
    @Query("SELECT COALESCE(SUM(p.point), 0) " +
            "FROM PointHistory p " +
            "WHERE p.member.memberIdx = :memberId " +
            "AND p.insertTime BETWEEN :start AND :end")
    Long sumPointsInPeriod(@Param("memberId") Long memberId,
                           @Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);

    // 오늘 특정 액션 횟수 (예: POST, COMMENT)
    @Query(value = "SELECT COUNT(*) " +
            "FROM POINT_HISTORY p " +
            "WHERE p.MEMBER_IDX = :memberIdx " +
            "AND p.ACTION_TYPE = :actionType " +
            "AND TRUNC(p.INSERT_TIME) = TRUNC(SYSDATE)",
            nativeQuery = true)
    Long countTodayActions(@Param("memberIdx") Long memberIdx,
                           @Param("actionType") String actionType);
}
