package com.simplecoding.cheforest.jpa.board.repository;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.board.entity.Board;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {

    // 카테고리별 검색 + 제목 검색은 JpaSpecificationExecutor/QueryDSL로 처리

//    전체 게시글 수
    long count();

//    카테고리별 게시글 수
    long countByCategory(String category);

//    푸터용 카테고리
    @Query("SELECT b.category, COUNT(b) FROM Board b GROUP BY b.category")
    List<Object[]> countBoardsByCategory();

    // 조회수 증가
    @Modifying
    @Query("update Board b set b.viewCount = b.viewCount + 1 where b.boardId = :boardId")
    void increaseViewCount(@Param("boardId") Long boardId);

    // 인기글 조회 (조회수 기준, 중복 제거)
    @Query("SELECT DISTINCT b FROM Board b ORDER BY b.viewCount DESC")
    List<Board> findTop5ByOrderByLikeCountDesc();

    // 제목 검색 (중복 제거)
    @Query("SELECT DISTINCT b FROM Board b WHERE b.title LIKE %:keyword%")
    List<Board> searchBoards(@Param("keyword") String keyword, Pageable pageable);

    // 검색 로직
    @Query("SELECT b FROM Board b WHERE " +
            // 1. 카테고리 필수 필터링 조건
            "b.category = :category AND " +
            // 2. 검색어 옵션 처리: 검색어가 없으면 (NULL이거나 빈 문자열) 전체를 반환하고, 있으면 LIKE 검색을 수행
            "(:keyword IS NULL OR :keyword = '' OR b.title LIKE %:keyword% OR b.prepare LIKE %:keyword%)" +
            // 3. 정렬 (게시글 ID 내림차순, 즉 최신순)
            "ORDER BY b.boardId DESC")
    List<Board> searchByCategoryAndKeyword(
            @Param("category") String category,
            @Param("keyword") String keyword);



    // 🔥 insertTime 기준으로 가장 최근 3건 조회
    List<Board> findTop3ByOrderByInsertTimeDesc();

    @Query("select b.boardId, b.category, b.thumbnail from Board b where b.boardId in :ids")
    List<Object[]> findMetaByIds(@Param("ids") List<Long> ids);

    // 카테고리별 랜덤 1개 게시글
    @Query(value = """
    SELECT * 
    FROM BOARD b
    WHERE b.CATEGORY = :category
    ORDER BY DBMS_RANDOM.VALUE
    FETCH FIRST :limit ROWS ONLY
    """, nativeQuery = true)
    List<Board> findRandomBoardsByCategory(@Param("category") String category,
                                           @Param("limit") int limit);


}
