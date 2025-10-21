package com.simplecoding.cheforest.jpa.like.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {  // 내부 서비스용

    private Long likeId;        // 좋아요 PK
    private Long memberIdx;     // 회원 FK
    private Long boardId;       // 게시판 글 ID
    private String recipeId;    // 레시피 ID
    private Long reviewId;      // 댓글 ID
    private String likeType;    // BOARD / RECIPE / REVIEW
    private LocalDateTime likeDate; // 등록일
    private Long likeCount;      // 좋아요 수 (조회 전용)
}
