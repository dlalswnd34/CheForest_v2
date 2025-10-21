package com.simplecoding.cheforest.jpa.review.service;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.board.entity.Board;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import com.simplecoding.cheforest.jpa.like.repository.LikeRepository;
import com.simplecoding.cheforest.jpa.review.dto.ReviewDto;
import com.simplecoding.cheforest.jpa.review.entity.Review;
import com.simplecoding.cheforest.jpa.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MapStruct mapStruct;
    private final LikeRepository likeRepository;

    /** 댓글 저장 */
    @Transactional
    public ReviewDto save(ReviewDto dto) {
        Review review = mapStruct.toEntity(dto);

        if (dto.getBoardId() != null) {
            Board board = new Board();
            board.setBoardId(dto.getBoardId());
            review.setBoard(board);
        } else {
            throw new IllegalArgumentException("boardId가 없습니다.");
        }

        // likeCount 초기화
        if (review.getLikeCount() == null) {
            review.setLikeCount(0L);
        }

        Review saved = reviewRepository.save(review);

        // DTO 변환
        ReviewDto result = mapStruct.toDto(saved);
        result.setBoardId(saved.getBoard().getBoardId());
        result.setWriterIdx(saved.getWriterIdx());

        // Member 정보 채우기
        Member writer = memberRepository.findById(saved.getWriterIdx())
                .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));
        result.setNickname(writer.getNickname());
        result.setGrade(writer.getGrade());
        result.setProfile(writer.getProfile());

        return result;
    }

    /** 댓글 수정 */
    @Transactional
    public ReviewDto update(ReviewDto dto) {
        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. ID=" + dto.getReviewId()));

        mapStruct.updateEntityFromDto(dto, review);
        review.setUpdateTime(LocalDateTime.now());

        Review updated = reviewRepository.save(review);

        ReviewDto result = mapStruct.toDto(updated);

        // Member 정보 다시 세팅
        Member writer = memberRepository.findById(updated.getWriterIdx())
                .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));
        result.setNickname(writer.getNickname());
        result.setGrade(writer.getGrade());
        result.setProfile(writer.getProfile());

        return result;
    }

    /** 게시글별 댓글 + 대댓글 조회 */
    @Transactional(readOnly = true)
    public List<ReviewDto> getCommentsWithReplies(Long boardId) {
        List<Review> parents = reviewRepository.findByBoard_BoardIdAndParentIdIsNullOrderByInsertTimeAsc(boardId);

        return parents.stream().map(parent -> {
            ReviewDto parentDto = mapStruct.toDto(parent);

            Member parentWriter = memberRepository.findById(parent.getWriterIdx())
                    .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));
            parentDto.setNickname(parentWriter.getNickname());
            parentDto.setGrade(parentWriter.getGrade());
            parentDto.setProfile(parentWriter.getProfile());

            // 대댓글 처리
            List<ReviewDto> replyDtos = reviewRepository.findByParentIdOrderByInsertTimeAsc(parent.getReviewId())
                    .stream()
                    .map(reply -> {
                        ReviewDto replyDto = mapStruct.toDto(reply);
                        Member replyWriter = memberRepository.findById(reply.getWriterIdx())
                                .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));
                        replyDto.setNickname(replyWriter.getNickname());
                        replyDto.setGrade(replyWriter.getGrade());
                        replyDto.setProfile(replyWriter.getProfile());
                        return replyDto;
                    })
                    .collect(Collectors.toList());

            parentDto.setReplies(replyDtos);
            return parentDto;
        }).collect(Collectors.toList());
    }

    /** 게시글 삭제 시 댓글 전체 삭제 */
    @Transactional
    public void deleteByBoardId(Long boardId) {
        // 1️⃣ 게시글에 달린 모든 댓글 ID 조회
        List<Review> reviews = reviewRepository.findByBoard_BoardIdAndParentIdIsNullOrderByInsertTimeAsc(boardId);

        for (Review review : reviews) {
            // 1-1️⃣ 부모 댓글의 좋아요 삭제
            likeRepository.deleteAllByReviewId(review.getReviewId());

            // 1-2️⃣ 부모 댓글의 대댓글 목록 조회
            List<Review> replies = reviewRepository.findByParentIdOrderByInsertTimeAsc(review.getReviewId());
            for (Review reply : replies) {
                likeRepository.deleteAllByReviewId(reply.getReviewId()); // ✅ 대댓글 좋아요 삭제
            }
        }

        // 2️⃣ 댓글 + 대댓글 전부 삭제
        reviewRepository.deleteByBoard_BoardId(boardId);
    }

    /** 댓글 삭제 (단일) */
    @Transactional
    public void delete(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다. ID=" + reviewId);
        }
        likeRepository.deleteAllByReviewId(reviewId);
        reviewRepository.deleteById(reviewId);
    }
}
