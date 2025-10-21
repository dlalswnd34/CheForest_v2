package com.simplecoding.cheforest.jpa.like.entity;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "Likes")
@Table(name = "BOARD_LIKE",
        uniqueConstraints = {
                @UniqueConstraint(name="UQ_BOARD_LIKE",
                        columnNames = {"member_idx", "board_id", "recipe_id", "review_id"})
        })
@SequenceGenerator(
        name = "SEQ_BOARD_LIKE_JPA",
        sequenceName = "SEQ_BOARD_LIKE",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BOARD_LIKE_JPA")
    private Long likeId;   // PK (시퀀스)

    // 회원 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_IDX", nullable = false)
    private Member member;

    private Long boardId;   // 게시글 ID (nullable)

    private String recipeId;   // 레시피 ID (nullable)

    private Long reviewId;     // 리뷰 ID (nullable)

    private String likeType;   // BOARD / RECIPE

    @Column(name = "LIKE_DATE", nullable = false, updatable = false)
    private LocalDateTime likeDate; // 등록일 (SYSDATE)


    @PrePersist
    public void prePersist() {
        if (this.likeDate == null) {
            this.likeDate = LocalDateTime.now();
        }
    }
}
