package com.simplecoding.cheforest.es.integratedSearch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSaveReq {
    private String category;   // 카테고리
    private String title;      // 제목
    private String prepare;    // 준비물
    private String content;    // 내용
    private String thumbnail;  // 썸네일 이미지 경로
    private String cooktime;     // 조리시간
    private String difficulty;   // 난이도
}
