package com.simplecoding.cheforest.jpa.board.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListDto {  // 목록조회 dto
    private Long boardId;
    private String category;
    private String title;
    private String nickname;
    private Long writerIdx;
    private Long viewCount;
    private Long likeCount;
    private String thumbnail;
    private LocalDateTime insertTime;
    private String cooktime;
    private String difficulty;
    private String createdAgo;
}
