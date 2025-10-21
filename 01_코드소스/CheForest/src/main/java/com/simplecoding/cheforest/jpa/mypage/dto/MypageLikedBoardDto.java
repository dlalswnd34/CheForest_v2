package com.simplecoding.cheforest.jpa.mypage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MypageLikedBoardDto {
    private Long boardId;
    private String title;
    private String writerName;      // 작성자 닉네임
    private LocalDateTime writeDate;// 글 작성 시각 (BOARD.insertTime)
    private Long viewCount;
    private Long likeCount;

    // 추가 필드
    private String category;        // BOARD.category
    private String thumbnail;       // BOARD.thumbnail
    private LocalDateTime likeDate; // 좋아요 누른 시각 (BOARD_LIKE.insertTime)

    // JPQL에서 사용할 생성자 (필드 순서에 맞춰 주세요)
    public MypageLikedBoardDto(Long boardId, String title, String writerName,
                               LocalDateTime writeDate, Long viewCount, Long likeCount,
                               String category, String thumbnail, LocalDateTime likeDate) {
        this.boardId = boardId;
        this.title = title;
        this.writerName = writerName;
        this.writeDate = writeDate;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.category = category;
        this.thumbnail = thumbnail;
        this.likeDate = likeDate;
    }
}
