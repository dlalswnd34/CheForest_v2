package com.simplecoding.cheforest.jpa.admin.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonthlyJoinCountDTO {
    private String month;
    private int count;
}
