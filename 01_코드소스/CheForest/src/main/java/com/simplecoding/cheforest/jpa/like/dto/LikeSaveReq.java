package com.simplecoding.cheforest.jpa.like.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LikeSaveReq {   // 좋아요 요청 (클릭/취소 컨트롤러용)
    private Long memberIdx;
    private Long boardId;    // BOARD일 때만 사용
    private String recipeId; // RECIPE일 때만 사용
    private Long reviewId;   // REVIEW일 때만 사용
    private String likeType; // BOARD / RECIPE / REVIEW
}
