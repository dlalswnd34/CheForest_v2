package com.simplecoding.cheforest.jpa.admin.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonthlyActivityDTO {
    private String month;      // "YYYY-MM"
    private int boardCount;    // 게시글 수
    private int memberCount;   // 가입자 수
}
