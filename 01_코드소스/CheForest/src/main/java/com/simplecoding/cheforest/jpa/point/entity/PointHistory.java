package com.simplecoding.cheforest.jpa.point.entity;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "POINT_HISTORY")
@SequenceGenerator(
        name = "POINT_HISTORY_JPA",
        sequenceName = "POINT_HISTORY_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POINT_HISTORY_JPA")
    private Long pointId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_IDX", nullable = false)
    private Member member;  // 회원 (FK)

    private String actionType;       // 활동 종류 (게시글, 댓글, 게임 등)
    private Long point = 0L;         // 적립 포인트
    private LocalDateTime insertTime = LocalDateTime.now(); // 적립 시각
}
