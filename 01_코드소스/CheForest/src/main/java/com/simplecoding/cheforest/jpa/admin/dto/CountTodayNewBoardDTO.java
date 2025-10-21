package com.simplecoding.cheforest.jpa.admin.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CountTodayNewBoardDTO {
    private int boardCount;
    private int reviewCount;
}
