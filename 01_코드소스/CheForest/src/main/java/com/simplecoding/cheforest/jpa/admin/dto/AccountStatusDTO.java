package com.simplecoding.cheforest.jpa.admin.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountStatusDTO {
    private Long activeCount;     // 활동 계정 수
    private Long dormantCount;    // 휴면 계정 수
    private Long suspendedCount;  // 제재 계정 수
    private int activePercent;    // 활동 계정 수 퍼센트
    private int dormantPercent;   // 휴면 계정 수 퍼센트
    private int suspendedPercent; // 제재 계정 수 퍼센트
}
