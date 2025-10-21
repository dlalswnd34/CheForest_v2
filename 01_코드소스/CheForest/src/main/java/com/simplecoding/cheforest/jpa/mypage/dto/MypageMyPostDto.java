package com.simplecoding.cheforest.jpa.mypage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MypageMyPostDto {
    private Long boardId;
    private String title;
    private LocalDateTime insertTime;
    private Long viewCount;
    private Long likeCount;
    private String category;   // 추가
    private String thumbnail;  // 기존 유지

    // JPQL에서 사용할 생성자 (순서·타입 레포지토리 쿼리와 반드시 동일)
    public MypageMyPostDto(Long boardId,
                           String title,
                           LocalDateTime insertTime,
                           Long viewCount,
                           Long likeCount,
                           String category,
                           String thumbnail) {
        this.boardId = boardId;
        this.title = title;
        this.insertTime = insertTime;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.category = category;
        this.thumbnail = thumbnail;
    }
}
