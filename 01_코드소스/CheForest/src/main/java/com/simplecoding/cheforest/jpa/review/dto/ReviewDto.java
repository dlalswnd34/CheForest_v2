package com.simplecoding.cheforest.jpa.review.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDto {

    private Long reviewId;     // 댓글 ID
    private Long boardId;      // 게시글 ID
    private Long writerIdx;    // 작성자 ID
    private String nickname;   // 작성자 닉네임 추가
    private String grade;      // 작성자 등급
    private String profile;    // 작성자 프로필 이미지
    private String content;    // 내용
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
    private Long parentId;     // 부모 댓글 ID
    private Long likeCount;

    private List<ReviewDto> replies = new ArrayList<>();
}

