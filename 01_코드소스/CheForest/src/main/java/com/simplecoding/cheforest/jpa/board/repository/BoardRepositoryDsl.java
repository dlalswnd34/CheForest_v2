package com.simplecoding.cheforest.jpa.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simplecoding.cheforest.jpa.auth.entity.QMember;
import com.simplecoding.cheforest.jpa.board.dto.BoardDetailDto;
import com.simplecoding.cheforest.jpa.board.dto.BoardLatestRowDTO;
import com.simplecoding.cheforest.jpa.board.dto.BoardListDto;

import com.simplecoding.cheforest.jpa.board.entity.QBoard;
import com.simplecoding.cheforest.jpa.review.entity.QReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardRepositoryDsl {

    private final JPAQueryFactory queryFactory;

    // 목록 조회
    public Page<BoardListDto> searchBoards(String keyword, String category, String searchType, Pageable pageable) {
        QBoard board = QBoard.board;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        // 1) 카테고리 필터링
        if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
            builder.and(board.category.eq(category.trim()));
        }

        // 2) 검색어 필터링 : searchType에 따라 조건 분기
        if (keyword != null && !keyword.isBlank()) {
            if ("ingredient".equalsIgnoreCase(searchType)) {
                // searchType이 ingredient일 때: 재료(prepare) 검색
                builder.and(board.prepare.containsIgnoreCase(keyword));
            } else {
                // searchType이 title이거나 정의되지 않았을 때: 제목(title) 검색 (기본값)
                builder.and(board.title.containsIgnoreCase(keyword));
            }
        }

        List<BoardListDto> content = queryFactory
                .select(Projections.bean(BoardListDto.class,
                        board.boardId.as("boardId"),
                        board.category.as("category"),
                        board.title.as("title"),
                        member.nickname.as("nickname"),
                        member.memberIdx.as("writerIdx"),
                        board.viewCount.as("viewCount"),
                        board.likeCount.as("likeCount"),
                        board.thumbnail.as("thumbnail"),
                        board.insertTime.as("insertTime")
                ))
                .from(board)
                .leftJoin(board.writer, member)
                .where(builder)
                .orderBy(board.insertTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(board.count())
                .from(board)
                .where(builder)
                .fetchOne();

        log.info(">>> [searchBoards] Debug Log");
        log.info(">>> Category Param: {}", category);
        log.info(">>> Keyword Param: {}", keyword);
        log.info(">>> SearchType Param: {}", searchType);
        log.info(">>> Total Count from Query: {}", total);
        log.info(">>> Content Size from Query: {}", content.size());

        return new PageImpl<>(content, pageable, total);
    }

    // 상세 조회
    public BoardDetailDto findBoardDetail(Long boardId) {
        QBoard  board = QBoard.board;
        QMember member = QMember.member;

        return queryFactory
                .select(Projections.bean(BoardDetailDto.class,
                        board.boardId.as("boardId"),
                        board.category.as("category"),
                        board.title.as("title"),
                        board.prepare.as("prepare"),
                        board.prepareAmount.as("prepareAmount"),
                        board.content.as("content"),
                        board.thumbnail.as("thumbnail"),
                        member.nickname.as("nickname"),
                        member.profile.as("profile"),
                        member.memberIdx.as("writerIdx"),
                        board.viewCount.as("viewCount"),
                        board.likeCount.as("likeCount"),
                        board.insertTime.as("insertTime"),
                        board.updateTime.as("updateTime"),
                        member.grade.as("grade"),
                        board.cookTime.as("cookTime"),
                        board.difficulty.as("difficulty")
                ))
                .from(board)
                .join(board.writer, member)
                .where(board.boardId.eq(boardId))
                .fetchOne();
    }

    // 인기글 TOP3
    public List<BoardListDto> findBestPosts() {
        QBoard board = QBoard.board;
        QMember member = QMember.member;

        return queryFactory
                .select(Projections.bean(BoardListDto.class,
                        board.boardId.as("boardId"),
                        board.category.as("category"),
                        board.title.as("title"),
                        member.nickname.as("nickname"),
                        member.memberIdx.as("writerIdx"),
                        board.viewCount.as("viewCount"),
                        board.likeCount.as("likeCount"),
                        board.thumbnail.as("thumbnail"),
                        board.insertTime.as("insertTime")
                ))
                .from(board)
                .join(board.writer, member)
                .orderBy(board.likeCount.desc(), board.boardId.desc())
                .limit(3)
                .fetch();
    }

    // 카테고리별 인기글 TOP3
    public List<BoardListDto> findBestPostsByCategory(String category) {
        QBoard board = QBoard.board;
        QMember member = QMember.member;

        return queryFactory
                .select(Projections.bean(BoardListDto.class,
                        board.boardId.as("boardId"),
                        board.category.as("category"),
                        board.title.as("title"),
                        member.nickname.as("nickname"),
                        member.memberIdx.as("writerIdx"),
                        board.viewCount.as("viewCount"),
                        board.likeCount.as("likeCount"),
                        board.thumbnail.as("thumbnail"),
                        board.insertTime.as("insertTime")
                ))
                .from(board)
                .join(board.writer, member)
                .where(board.category.eq(category))
                .orderBy(board.likeCount.desc(), board.boardId.desc())
                .limit(3)
                .fetch();
    }

    // 카테고리별 최신글 + 댓글수
    public List<BoardLatestRowDTO> findLatestByCategory(String category, int limit) {
        QBoard board = QBoard.board;
        QMember member = QMember.member;
        QReview review = QReview.review;

        return queryFactory
                .select(Projections.bean(BoardLatestRowDTO.class,
                        board.boardId.as("id"),
                        board.title.as("title"),
                        member.nickname.coalesce("알 수 없음").as("writerNickname"),
                        board.insertTime.as("insertTime"),
                        ExpressionUtils.as(
                                JPAExpressions.select(review.count())
                                        .from(review)
                                        .where(review.board.boardId.eq(board.boardId)),
                                "commentCount"
                        )
                ))
                .from(board)
                .leftJoin(board.writer, member)
                .where(board.category.eq(category))
                .orderBy(board.insertTime.desc())
                .limit(limit)
                .fetch();
    }
}
