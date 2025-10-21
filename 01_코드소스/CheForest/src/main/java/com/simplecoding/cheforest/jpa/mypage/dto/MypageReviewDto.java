package com.simplecoding.cheforest.jpa.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MypageReviewDto {
    private Long reviewId;
    private Long boardId;
    private String boardTitle;     // 댓글이 달린 글 제목
    private String content;        // 댓글 내용
    private java.util.Date insertTime; // ← Date
    private java.util.Date updateTime; // ← Date
}
