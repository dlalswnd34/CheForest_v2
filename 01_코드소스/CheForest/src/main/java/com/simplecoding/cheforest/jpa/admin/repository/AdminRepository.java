package com.simplecoding.cheforest.jpa.admin.repository;


import com.simplecoding.cheforest.jpa.admin.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// made by yes_ung
@Repository
public class AdminRepository {

    @PersistenceContext
    private EntityManager em;

   //  총 가입자 수
    public long getMemberCount() {
        String sql = "SELECT COUNT(*) FROM MEMBER WHERE ROLE <> 'LEFT'";
        Object result = em.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue(); 
    }

//    전체 레시피 수 (게시판 + API_RECIPE 합산)
public long getTotalRecipeCount() {
    String sql = "SELECT (SELECT COUNT(*) FROM BOARD) + (SELECT COUNT(*) FROM API_RECIPE) FROM DUAL";
    Object result = em.createNativeQuery(sql).getSingleResult();
    return ((Number) result).longValue();
}

    // 오늘 가입한 회원 수
    public long getTodayMemberCount() {
        String sql = "SELECT COUNT(*) FROM MEMBER WHERE TRUNC(INSERT_TIME) = TRUNC(SYSDATE)";
        Object result = em.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    // 오늘 가입한 회원 전체 리스트 (전체 컬럼 가져오기, Object[] 형태로 받음)
    public List<TodaySignUpUsersDto> getTodayMemberList() {
        String sql = "SELECT * FROM MEMBER WHERE TRUNC(INSERT_TIME) = TRUNC(SYSDATE)";
        List<Object[]> results = em.createNativeQuery(sql).getResultList();
        return results.stream()
                .map(row -> new TodaySignUpUsersDto(
                        ((Number) row[0]).longValue(),  // ID
                        (String) row[1],                // NAME
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (java.sql.Timestamp) row[6],
                        (String) row[7]
                ))
                .collect(Collectors.toList());
    }
//       활동,휴먼,제재 계정수 집계
    public AccountStatusDTO getAccountStatusCounts(){
        String sql = "SELECT 계정상태, COUNT(*) AS 인원수\n" +
                "    FROM (\n" +
                "        SELECT\n" +
                "            CASE\n" +
                "                WHEN SUSPENSION IS NOT NULL THEN '제재 계정'\n" +
                "                WHEN LAST_LOGIN_TIME >= ADD_MONTHS(SYSDATE, -1) THEN '활동 계정'\n" +
                "                ELSE '휴면 계정'\n" +
                "            END AS 계정상태\n" +
                "        FROM MEMBER\n" +
                "    WHERE ROLE <> 'LEFT'\n" +
                "    )\n" +
                "    GROUP BY 계정상태";
        List<Object[]> results = em.createNativeQuery(sql).getResultList();
       // 초기값 0으로 세팅
        long active = 0;
        long dormant = 0;
        long suspended = 0;

        for (Object[] row : results) {
            String status = (String) row[0];
            Number count = (Number) row[1];

            switch (status) {
                case "활동 계정" -> active = count.longValue();
                case "휴면 계정" -> dormant = count.longValue();
                case "제재 계정" -> suspended = count.longValue();
            }
        }
        long total = active + dormant + suspended;
        int activePercent = total > 0 ? (int) Math.round((active * 100.0) / total) : 0;
        int dormantPercent = total > 0 ? (int) Math.round((dormant * 100.0) / total) : 0;
        int suspendedPercent = total > 0 ? (int) Math.round((suspended * 100.0) / total) : 0;


        return new AccountStatusDTO(active, dormant, suspended, activePercent, dormantPercent, suspendedPercent);
    }

    public CountTodayNewBoardDTO getCountTodayNewBoard() {
        String sql = "SELECT \n" +
                "    (SELECT COUNT(*) \n" +
                "     FROM BOARD \n" +
                "     WHERE TRUNC(INSERT_TIME) = TRUNC(SYSDATE)) AS TODAY_BOARD_COUNT,\n" +
                "    \n" +
                "    (SELECT COUNT(*) \n" +
                "     FROM BOARD_REVIEW \n" +
                "     WHERE TRUNC(INSERT_TIME) = TRUNC(SYSDATE)) AS TODAY_REVIEW_COUNT\n" +
                "FROM DUAL\n";

        Object[] result = (Object[]) em.createNativeQuery(sql).getSingleResult();

        int boardCount = ((Number) result[0]).intValue();
        int reviewCount = ((Number) result[1]).intValue();
        return new CountTodayNewBoardDTO(boardCount, reviewCount);
    }
    public List<MonthlyBoardCountDTO> getMonthlyBoardCount() {
        String sql =
                "WITH months AS ( " +
                        "    SELECT TO_CHAR(ADD_MONTHS(TRUNC(SYSDATE, 'MM'), LEVEL - 5), 'YYYY-MM') AS month " +
                        "    FROM DUAL CONNECT BY LEVEL <= 5 " +
                        ") " +
                        "SELECT m.month, NVL(COUNT(b.BOARD_ID), 0) AS count " +
                        "FROM months m " +
                        "LEFT JOIN BOARD b ON TO_CHAR(b.INSERT_TIME, 'YYYY-MM') = m.month " +
                        "GROUP BY m.month " +
                        "ORDER BY m.month ";

        List<Object[]> results = em.createNativeQuery(sql).getResultList();

        return results.stream()
                .map(row -> new MonthlyBoardCountDTO(
                        (String) row[0],
                        ((Number) row[1]).intValue()))
                .collect(Collectors.toList());
    }
    public List<MonthlyJoinCountDTO> getMonthlyJoinCount() {
        String sql =
                "WITH months AS ( " +
                        "    SELECT TO_CHAR(ADD_MONTHS(TRUNC(SYSDATE, 'MM'), LEVEL - 5), 'YYYY-MM') AS month " +
                        "    FROM DUAL CONNECT BY LEVEL <= 5 " +
                        ") " +
                        "SELECT m.month, NVL(COUNT(mem.MEMBER_IDX), 0) AS count " +
                        "FROM months m " +
                        "LEFT JOIN MEMBER mem ON TO_CHAR(mem.INSERT_TIME, 'YYYY-MM') = m.month " +
                        "GROUP BY m.month " +
                        "ORDER BY m.month ";

        List<Object[]> results = em.createNativeQuery(sql).getResultList();

        return results.stream()
                .map(row -> new MonthlyJoinCountDTO(
                        (String) row[0],
                        ((Number) row[1]).intValue()))
                .collect(Collectors.toList());
    }


    public Map<String, Integer> getMonthlyBoardCountMap() {
        String sql =  "WITH months AS ( " +
                "    SELECT TO_CHAR(ADD_MONTHS(TRUNC(SYSDATE, 'MM'), LEVEL - 5), 'YYYY-MM') AS month " +
                "    FROM DUAL CONNECT BY LEVEL <= 5 " +
                ") " +
                "SELECT m.month, NVL(COUNT(b.BOARD_ID), 0) AS count " +
                "FROM months m " +
                "LEFT JOIN BOARD b ON TO_CHAR(b.INSERT_TIME, 'YYYY-MM') = m.month " +
                "GROUP BY m.month " +
                "ORDER BY m.month ";
        List<Object[]> results = em.createNativeQuery(sql).getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    public Map<String, Integer> getMonthlyJoinCountMap() {
        String sql =  "WITH months AS ( " +
                "    SELECT TO_CHAR(ADD_MONTHS(TRUNC(SYSDATE, 'MM'), LEVEL - 5), 'YYYY-MM') AS month " +
                "    FROM DUAL CONNECT BY LEVEL <= 5 " +
                ") " +
                "SELECT m.month, NVL(COUNT(mem.MEMBER_IDX), 0) AS count " +
                "FROM months m " +
                "LEFT JOIN MEMBER mem ON TO_CHAR(mem.INSERT_TIME, 'YYYY-MM') = m.month " +
                "GROUP BY m.month " +
                "ORDER BY m.month ";
        List<Object[]> results = em.createNativeQuery(sql).getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }
    public String getMonthlyActivityStats() {
        Map<String, Integer> boardMap = getMonthlyBoardCountMap();
        Map<String, Integer> memberMap = getMonthlyJoinCountMap();

        // 모든 월을 기준으로 정렬된 리스트 생성
        List<String> allMonths = boardMap.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
       //    DTO에 내용담기
        List<MonthlyActivityDTO> result = new ArrayList<>();
        for (String month : allMonths) {
            int boardCount = boardMap.getOrDefault(month, 0);
            int memberCount = memberMap.getOrDefault(month, 0);
            result.add(new MonthlyActivityDTO(month, boardCount, memberCount));
        }
       //    json 형태로 변환(js에 값 넘기기 위함)
        List<String> jsonItems = result.stream()
                .map(dto -> String.format(
                        "{ month: '%s', boardCount: %d, memberCount: %d }",
                        dto.getMonth(), dto.getBoardCount(), dto.getMemberCount()
                ))
                .collect(Collectors.toList());

        String monthlyActivityData = "[" + String.join(",", jsonItems) + "]";



        return monthlyActivityData;
    }
    // 자주 묻는 문의로 등록한 문의들 (Object[] 형태로 받음)*clob 형태인데 이 방식으론 String으로 받으려면 4000자까지 밖에 못 받음
    public List<InquiriesIsFaqDto> getInquiriesIsFaqDto() {
        String sql = "SELECT TITLE, DBMS_LOB.SUBSTR(ANSWER_CONTENT, 4000, 1) AS ANSWER_CONTENT FROM INQUIRIES WHERE IS_FAQ = 'Y'";
        List<Object[]> results = em.createNativeQuery(sql).getResultList();
//       자료형태 디버깅용
//        for (Object[] row : results) {
//            System.out.println("row[0] type: " + row[0].getClass());
//            System.out.println("row[1] type: " + row[1].getClass());
//        }
        return results.stream()
                .map(row -> new InquiriesIsFaqDto(
                        (String) row[0],
                        (String) row[1]
                ))
                .collect(Collectors.toList());
    }
    //  자주 묻는 문의로 등록한 문의들 수
    public long getInquiriesIsFaqCount() {
        String sql = "SELECT COUNT(*) FROM INQUIRIES WHERE IS_FAQ = 'Y'";
        Object result = em.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }








}
