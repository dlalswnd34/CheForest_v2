package com.simplecoding.cheforest.jpa.file.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileDto {
    private Long fileId;             // 파일 아이디 (PK)
    private String fileName;     // 원본 파일명
    private String filePath;     // 저장 경로
    private String fileType;     // 확장자
    private String useType;      // 사용 목적 (예: BOARD, PROFILE)
    private Long useTargetId;    // 대상 ID (게시글 번호 등)
    private String usePosition;  // 위치 구분 (썸네일, 본문 등)

    // BaseTimeEntity 에서 상속받는 값들
    private LocalDateTime insertTime; // 등록일시
    private LocalDateTime updateTime; // 수정일시

    // 업로더 회원 ID (Member 참조 대신 DTO는 단순 ID만 가짐)
    private Long uploaderIdx;        // 업로더 회원 PK
    private String uploaderLoginId;  // 업로더 로그인 ID
}
