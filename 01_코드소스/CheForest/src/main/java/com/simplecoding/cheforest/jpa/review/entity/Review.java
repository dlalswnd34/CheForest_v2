package com.simplecoding.cheforest.jpa.review.entity;

import com.simplecoding.cheforest.jpa.board.entity.Board;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD_REVIEW")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq_gen")
    @SequenceGenerator(
            name = "review_seq_gen",
            sequenceName = "BOARD_REVIEW_SEQ",
            allocationSize = 1
    )
    @Column(name = "REVIEW_ID")
    private Long reviewId;

    // ================= 연관관계 추가 =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    private Board board;   // 게시글 엔티티와 연관관계

    @Column(name = "WRITER_IDX", nullable = false)
    private Long writerIdx; // 작성자 ID

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content; // 댓글 내용

    @Column(name = "INSERT_TIME")
    private LocalDateTime insertTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @Column(name = "PARENT_ID")
    private Long parentId; // 부모 댓글 (NULL = 댓글, 값 있으면 대댓글)

    private Long likeCount = 0L;

    @PrePersist
    protected void onCreate() {
        this.insertTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
