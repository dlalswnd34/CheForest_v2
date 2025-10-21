package com.simplecoding.cheforest.jpa.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepDto {
    private String text;   // 조리법 설명
    private String image;  // 이미지 경로 (없으면 null)
}
