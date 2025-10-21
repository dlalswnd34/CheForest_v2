package com.simplecoding.cheforest.jpa.board.entity;

import com.simplecoding.cheforest.jpa.common.BaseTimeEntity;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BOARD")
@SequenceGenerator(
        name = "BOARD_SEQ_JPA",
        sequenceName = "BOARD_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_JPA")
    private Long boardId;  // 게시글 ID (PK)

    private String category;
    private String title;
    private String content;
    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER_IDX", nullable = false)
    private Member writer;

    private String prepare;
    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Long likeCount = 0L;

    private String cookTime;
    private String difficulty;
    private String prepareAmount;
}
