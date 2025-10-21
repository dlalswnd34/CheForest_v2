package com.simplecoding.cheforest.jpa.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardLatestRowDTO {
    private Long  id;               // 게시글 PK
    private String title;           // 제목
    private String writerNickname;  // 작성자
    private java.time.LocalDateTime insertTime; // ← 엔티티의 작성시간 그대로 받기
    private String createdAgo;                  // ← Service에서 가공해서 채우기 "3시간 전" 같이 가공해둔 문자열
    private Long  commentCount;     // 댓글 수
}