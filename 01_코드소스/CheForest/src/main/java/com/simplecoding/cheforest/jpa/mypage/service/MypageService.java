package com.simplecoding.cheforest.jpa.mypage.service;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.board.service.BoardService;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageLikedBoardDto;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageLikedRecipeDto;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageMyPostDto;
import com.simplecoding.cheforest.jpa.mypage.dto.MypageReviewDto;
import com.simplecoding.cheforest.jpa.mypage.repository.MypageRepository;
import com.simplecoding.cheforest.jpa.point.service.PointService;
import com.simplecoding.cheforest.jpa.review.entity.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.simplecoding.cheforest.jpa.review.repository.ReviewRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {

    private final ReviewRepository reviewRepository;
    private final MypageRepository mypageRepository;


    // ===== 내가 작성한 글 =====
    public Page<MypageMyPostDto> getMyPosts(Long memberIdx, String keyword, Pageable pageable) {
        log.info("내가 작성한 글 조회 - memberIdx={}, keyword={}", memberIdx, keyword);
        return mypageRepository.findMyPosts(memberIdx, keyword, pageable);
    }

    public long getMyPostsCount(Long memberIdx, String keyword) {
        return mypageRepository.countMyPosts(memberIdx, keyword);
    }

    public long getMyPostsTotalViewCount(Long memberIdx) {
        return mypageRepository.sumMyPostsViewCount(memberIdx);
    }

    // ===== 내가 좋아요한 게시글 =====
    public Page<MypageLikedBoardDto> getLikedBoards(Long memberIdx, String keyword, Pageable pageable) {
        return mypageRepository.findLikedBoards(memberIdx, keyword, pageable);
    }

    public long getLikedBoardsCount(Long memberIdx, String keyword) {
        return mypageRepository.countLikedBoards(memberIdx, keyword);
    }

    // ===== 내가 좋아요한 레시피 =====
    public Page<MypageLikedRecipeDto> getLikedRecipes(Long memberIdx, String keyword, Pageable pageable) {
        return mypageRepository.findLikedRecipes(memberIdx, keyword, pageable);
    }

    public Page<MypageReviewDto> getMyReviews(Long memberIdx, Pageable pageable) {
        Page<Review> page = reviewRepository.findByWriterIdxOrderByInsertTimeDesc(memberIdx, pageable);

        return page.map(r -> {
            java.util.Date insertDate = (r.getInsertTime() == null)
                    ? null
                    : java.sql.Timestamp.valueOf(r.getInsertTime()); // LDT -> Date
            java.util.Date updateDate = (r.getUpdateTime() == null)
                    ? null
                    : java.sql.Timestamp.valueOf(r.getUpdateTime()); // LDT -> Date

            return new MypageReviewDto(
                    r.getReviewId(),
                    (r.getBoard() != null) ? r.getBoard().getBoardId() : null,
                    (r.getBoard() != null) ? r.getBoard().getTitle()   : null,
                    r.getContent(),
                    insertDate,
                    updateDate
            );
        });
    }
    // ===== 내가 작성한 댓글 수 =====
    public long getMyCommentsCount(Long memberIdx, String keyword) {
        return mypageRepository.countMyComments(memberIdx, keyword);
    }

    // ===== 합계 =====
    public long getMyCommentsTotalCount(Long memberIdx) {
        return mypageRepository.countMyComments(memberIdx, null);
    }

    public long getReceivedBoardLikes(Long memberIdx) {
        return mypageRepository.sumReceivedBoardLikes(memberIdx);
    }

//    가입일
    public String getMemberJoinDateText(Long memberIdx) {
        LocalDateTime dt = mypageRepository.findById(memberIdx)
                .map(Member::getInsertTime)
                .orElse(null);
        return (dt == null) ? null : dt.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"));
    }

    // ===== 활동 현황 집계 =====
    public record ActivityStats(
            long recipeCount,
            long commentCount,
            long likeCount,
            long weeklyPoints
    ) {}

    public ActivityStats getWeeklyActivityStats(Member member,
                                                PointService pointService,
                                                MypageRepository mypageRepository) {

        Long memberId = member.getMemberIdx();

        // 이번 주 월요일 00:00 ~ 일요일 23:59:59
        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek   = today.with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX);

        List<Object[]> result = mypageRepository.findWeeklyActivityStats(memberId, startOfWeek, endOfWeek);
        Object[] row = (result != null && !result.isEmpty()) ? result.get(0) : new Object[]{0L, 0L, 0L};

        long recipeCount  = row[0] == null ? 0L : ((Number) row[0]).longValue();
        long commentCount = row[1] == null ? 0L : ((Number) row[1]).longValue();
        long likeCount    = row[2] == null ? 0L : ((Number) row[2]).longValue();
        long weeklyPoints = pointService.getWeekPoints(memberId);

        return new ActivityStats(recipeCount, commentCount, likeCount, weeklyPoints);
    }
}
