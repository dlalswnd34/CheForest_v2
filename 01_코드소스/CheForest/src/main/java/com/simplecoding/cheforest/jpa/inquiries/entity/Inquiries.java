package com.simplecoding.cheforest.jpa.inquiries.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name="INQUIRIES")
@SequenceGenerator(
        name = "SEQ_INQUIRY_ID_JPA",
        sequenceName = "SEQ_INQUIRY_ID",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inquiries {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INQUIRY_ID_JPA")
    private Long inquiryId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_idx", insertable = false, updatable = false)
//    private Member member;  // 실제 오라클DB에 관계 안걸어나서 사용X 제약걸면 사용
//    ① FK 제약으로 인한 삭제 제약 방지
//    ② Lazy 로딩 성능 문제 회피
//    ③ 탈퇴 회원 게시글 유지 정책
//    ④ 개발/테스트 데이터 유연성 확보
    private Long memberIdx;

    private String title;
    private String questionContent;
    private String answerContent;
    private String answerStatus;
    private String isFaq;
    private Date createdAt;
    private Date answerAt;
    private Long likeCount;
}
