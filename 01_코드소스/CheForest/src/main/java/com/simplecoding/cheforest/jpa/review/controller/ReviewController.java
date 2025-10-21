package com.simplecoding.cheforest.jpa.review.controller;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.point.service.PointService;
import com.simplecoding.cheforest.jpa.review.dto.ReviewDto;
import com.simplecoding.cheforest.jpa.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final PointService pointService;
    private final MemberRepository memberRepository;

    // 댓글/대댓글 등록
    @PostMapping("/reviews")
    public ResponseEntity<ReviewDto> addReview(@RequestBody ReviewDto dto) {
        if (dto.getBoardId() == null || dto.getWriterIdx() == null) {
            return ResponseEntity.badRequest().build();
        }

        // 1) 댓글 저장
        ReviewDto saved = reviewService.save(dto);

        // 2) 포인트 적립 (댓글 작성자 기준)
        Member member = memberRepository.findById(dto.getWriterIdx())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        pointService.addPointWithLimit(member, "COMMENT");

        return ResponseEntity.ok(saved);
    }

    // 댓글 수정
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestBody ReviewDto dto) {
        try {
            dto.setReviewId(reviewId);
            return ResponseEntity.ok(reviewService.update(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 게시글별 댓글 + 대댓글 조회
    @GetMapping("/reviews/board/{boardId}")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long boardId) {
        return ResponseEntity.ok(reviewService.getCommentsWithReplies(boardId));
    }

    // 댓글 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.delete(reviewId);
            return ResponseEntity.ok(Map.of("message", "댓글 삭제 완료"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
