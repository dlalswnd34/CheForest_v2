package com.simplecoding.cheforest.jpa.file.controller;

import com.simplecoding.cheforest.jpa.file.dto.FileDto;
import com.simplecoding.cheforest.jpa.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /* ==============================
       1. 공통 삭제
    ============================== */
    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok().build();
    }

    /* ==============================
       2. 프로필 관련
    ============================== */

    // 프로필 업로드
    @PostMapping("/file/profile-upload")
    public ResponseEntity<FileDto> uploadProfileImage(
            @RequestParam("memberIdx") Long memberIdx,
            @RequestParam("profileImage") MultipartFile file
    ) throws IOException {
        // 기존 프로필 삭제
        FileDto oldProfile = fileService.getProfileFileByMemberId(memberIdx);
        if (oldProfile != null) {
            fileService.deleteFile(oldProfile.getFileId());
        }

        // 새 프로필 저장
        FileDto newFile = fileService.saveFile(file, "MEMBER", memberIdx, "PROFILE", memberIdx);

        // MemberService 에서 profile 컬럼 업데이트 필요
        return ResponseEntity.ok(newFile);
    }

    // 프로필 조회
    @GetMapping("/file/profile/{fileId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long fileId) {
        try {
            FileDto fileDto = fileService.getFile(fileId);
            if (fileDto == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileData = fileService.loadFileData(fileDto);
            return ResponseEntity.ok()
                    .header("Content-Type", fileService.getMimeType(fileDto.getFileType()))
                    .header("Content-Disposition", "inline; filename=\"" + fileDto.getFileName() + "\"")
                    .body(fileData);
        } catch (IOException e) {
            log.error("프로필 이미지 로드 실패", e);
            return ResponseEntity.status(500).build();
        }
    }

    /* ==============================
       3. 게시판 관련
    ============================== */

    // 게시판 파일 업로드
    @PostMapping("/file/board-upload")
    public ResponseEntity<List<FileDto>> uploadBoardFiles(
            @RequestParam("boardId") Long boardId,
            @RequestParam("uploaderId") Long uploaderId,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        Long firstFileId = fileService.saveBoardFiles(boardId, uploaderId, files);
        List<FileDto> fileList = fileService.getFilesByBoardId(boardId);
        return ResponseEntity.ok(fileList);
    }

    // 게시판 파일 목록 조회
    @GetMapping("/file/board/{boardId}")
    public ResponseEntity<List<FileDto>> getBoardFiles(@PathVariable Long boardId) {
        return ResponseEntity.ok(fileService.getFilesByBoardId(boardId));
    }

    // 게시판 파일 단건 다운로드
    @GetMapping("/file/board/file/{fileId}")
    public ResponseEntity<byte[]> downloadBoardFile(@PathVariable Long fileId) {
        try {
            FileDto fileDto = fileService.getFile(fileId);
            if (fileDto == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileData = fileService.loadFileData(fileDto);
            return ResponseEntity.ok()
                    .header("Content-Type", fileService.getMimeType(fileDto.getFileType()))
                    .header("Content-Disposition", "attachment; filename=\"" + fileDto.getFileName() + "\"")
                    .body(fileData);
        } catch (IOException e) {
            log.error("게시판 파일 다운로드 실패", e);
            return ResponseEntity.status(500).build();
        }
    }

    // 썸네일 미리보기
    @GetMapping("/file/board/preview/{fileId}")
    public ResponseEntity<byte[]> previewBoardFile(@PathVariable Long fileId) {
        try {
            FileDto fileDto = fileService.getFile(fileId);
            if (fileDto == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileData = fileService.loadFileData(fileDto);

            // MIME 추정
            String mime = fileService.getMimeType(fileDto.getFileType());
            if (mime == null || mime.isBlank()) mime = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            // inline 으로 내려서 <img src="..."> 에 바로 표시되게
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, mime)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDto.getFileName() + "\"")
                    .body(fileData);

        } catch (IOException e) {
            log.error("게시판 파일 미리보기 실패", e);
            return ResponseEntity.status(500).build();
        }
    }
}
