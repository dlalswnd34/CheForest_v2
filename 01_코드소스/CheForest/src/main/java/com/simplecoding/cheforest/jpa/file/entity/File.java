package com.simplecoding.cheforest.jpa.file.entity;

import com.simplecoding.cheforest.jpa.common.BaseTimeEntity;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UPLOAD_FILE")
@SequenceGenerator(
        name = "FILE_SEQ_JPA",
        sequenceName = "FILE_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_SEQ_JPA")
    private Long fileId;  // 파일 ID (PK)

    private String fileName;  // 원본 파일명

    private String filePath;  // 저장 경로

    private String fileType;  // 파일 확장자

    private String useType;   // 사용 목적 (예: BOARD, PROFILE)

    private Long useTargetId; // 대상 ID (게시글 번호 등)

    private String usePosition; // 위치 구분 (썸네일, 본문 등)

    // 업로더 회원 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPLOADER_ID")
    private Member uploader;
}
