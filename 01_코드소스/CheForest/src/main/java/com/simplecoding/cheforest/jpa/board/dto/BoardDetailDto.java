package com.simplecoding.cheforest.jpa.board.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailDto {  // 상세조회 dto
    private Long boardId;
    private String category;
    private String title;
    private String prepare;
    private String content;
    private String thumbnail;
    private String nickname;
    private String profile;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime insertTime;
    private String insertTimeStr;     // 날짜 포맷을 위해 추가함
    private LocalDateTime updateTime;
    private Long writerIdx;
    private String cookTime;
    private String difficulty;
    private String grade;
    private String prepareAmount;
}
