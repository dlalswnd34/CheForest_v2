package com.simplecoding.cheforest.jpa.like.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRes {     // 좋아요 응답 (조회/출력 컨트롤러용)
    private Long likeId;
    private String likeType;  // BOARD / RECIPE / REVIEW
    private Long boardId;
    private String recipeId;
    private Long reviewId;
    private Long likeCount;    // 게시글/레시피 좋아요 수

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime likeDate;
    private Long memberIdx;    // 좋아요 누른 회원 ID
    private String nickname;
}
