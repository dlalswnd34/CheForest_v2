package com.simplecoding.cheforest.jpa.mypage.repository;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageLikedBoardDto;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageLikedRecipeDto;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageMyPostDto;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MypageRepository extends JpaRepository<Member, Long> {

    // ===== 내가 작성한 글(검색어 O) =====
    @Query("""
           SELECT new com.simplecoding.cheforest.jpa.mypage.dto.MypageMyPostDto(
             b.boardId,
             b.title,
             b.insertTime,
             (b.viewCount + 0L),
             (b.likeCount + 0L),
             b.category,
             b.thumbnail
           )
           FROM Board b
           WHERE b.writer.memberIdx = :memberIdx
             AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
           ORDER BY b.insertTime DESC
           """)
    Page<MypageMyPostDto> findMyPosts(@Param("memberIdx") Long memberIdx,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);


    // ===== 내가 작성한 글 개수 =====
    @Query("""
           SELECT COUNT(b)
           FROM Board b
           WHERE b.writer.memberIdx = :memberIdx
             AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
           """)
    long countMyPosts(@Param("memberIdx") Long memberIdx,
                      @Param("keyword") String keyword);

    // ===== 내가 좋아요한 게시글 =====
// 내가 좋아요한 게시글 (목록)
    @Query("""
SELECT new com.simplecoding.cheforest.jpa.mypage.dto.MypageLikedBoardDto(
  b.boardId,
  b.title,
  m.nickname,
  b.insertTime,
  (b.viewCount + 0L),
  (b.likeCount + 0L),
  b.category,
  b.thumbnail,
  l.likeDate
)
FROM Likes l
JOIN Board b ON b.boardId = l.boardId
JOIN b.writer m
WHERE l.member.memberIdx = :memberIdx
  AND l.likeType = 'BOARD'
  AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
ORDER BY l.likeDate DESC
""")
    Page<MypageLikedBoardDto> findLikedBoards(@Param("memberIdx") Long memberIdx,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    // 내가 좋아요한 게시글 (개수)
    @Query("""
SELECT COUNT(l)
FROM Likes l
JOIN Board b ON b.boardId = l.boardId
WHERE l.member.memberIdx = :memberIdx
  AND l.likeType = 'BOARD'
  AND (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%'))
""")
    long countLikedBoards(@Param("memberIdx") Long memberIdx,
                          @Param("keyword") String keyword);

    // 내가 좋아요한 레시피
    @Query("""
SELECT new com.simplecoding.cheforest.jpa.mypage.dto.MypageLikedRecipeDto(
  r.recipeId,
  r.titleKr,
  r.categoryKr,
  r.likeCount,
  r.thumbnail,
  CONCAT('', FUNCTION('TO_CHAR', l.likeDate, 'YYYY.MM.DD'))
)
FROM Likes l
JOIN Recipe r ON l.recipeId = r.recipeId
WHERE l.member.memberIdx = :memberIdx
  AND l.likeType = 'RECIPE'
  AND (:keyword IS NULL OR r.titleKr LIKE CONCAT('%', :keyword, '%'))
ORDER BY l.likeDate DESC
""")
    Page<MypageLikedRecipeDto> findLikedRecipes(@Param("memberIdx") Long memberIdx,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);

    @Query("""
SELECT COUNT(l)
FROM Likes l
JOIN Recipe r ON l.recipeId = r.recipeId
WHERE l.member.memberIdx = :memberIdx
  AND l.likeType = 'RECIPE'
  AND (:keyword IS NULL OR r.titleKr LIKE CONCAT('%', :keyword, '%'))
""")
    long countLikedRecipes(@Param("memberIdx") Long memberIdx,
                           @Param("keyword") String keyword);

    // 내가 작성한 댓글
    @Query(value = """
  SELECT r.review_id      AS reviewId,
         b.board_id       AS boardId,
         b.title          AS boardTitle,
         r.content        AS content,
         r.insert_time    AS insertTime,
         r.update_time    AS updateTime
  FROM   BOARD_REVIEW r
  JOIN   BOARD b ON b.board_id = r.board_id
  WHERE  r.writer_idx = :memberIdx
  ORDER  BY r.insert_time DESC
""",
            countQuery = """
  SELECT COUNT(*)
  FROM BOARD_REVIEW r
  WHERE r.writer_idx = :memberIdx
""",
            nativeQuery = true)
    Page<MypageReviewDto> findMyReviews(@Param("memberIdx") Long memberIdx, Pageable pageable);

    // ===== 내가 작성한 댓글 수 =====
    @Query("""
           SELECT COUNT(r)
           FROM Review r
           WHERE r.writerIdx = :memberIdx
             AND (:keyword IS NULL OR r.content LIKE CONCAT('%', :keyword, '%'))
           """)
    long countMyComments(@Param("memberIdx") Long memberIdx,
                         @Param("keyword") String keyword);

    // ===== 합계 =====
    @Query("""
           SELECT COALESCE(SUM(b.likeCount + 0L), 0L)
           FROM Board b
           WHERE b.writer.memberIdx = :memberIdx
           """)
    long sumReceivedBoardLikes(@Param("memberIdx") Long memberIdx);

    // ===== 특정 회원(memberIdx)이 작성한 게시글들의 총 조회수를 합산 =====
    @Query("""
           SELECT COALESCE(SUM(b.viewCount + 0L), 0L)
           FROM Board b
           WHERE b.writer.memberIdx = :memberIdx
           """)
    long sumMyPostsViewCount(@Param("memberIdx") Long memberIdx);

    // ===== 금주 활동 통계 (게시글 수, 댓글 수, 받은 좋아요 수) =====
    @Query(value = """
        SELECT 
          NVL((SELECT COUNT(*) 
               FROM BOARD b 
               WHERE b.WRITER_IDX = :memberIdx 
                 AND b.INSERT_TIME BETWEEN :start AND :end), 0) AS postCount,
               
          NVL((SELECT COUNT(*) 
               FROM BOARD_REVIEW r 
               WHERE r.WRITER_IDX = :memberIdx 
                 AND r.INSERT_TIME BETWEEN :start AND :end), 0) AS commentCount,
               
          NVL((SELECT SUM(b.LIKE_COUNT)
               FROM BOARD b
               WHERE b.WRITER_IDX = :memberIdx 
                 AND b.INSERT_TIME BETWEEN :start AND :end), 0) AS likeCount
        FROM DUAL
        """, nativeQuery = true)
    List<Object[]> findWeeklyActivityStats(@Param("memberIdx") Long memberIdx,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}