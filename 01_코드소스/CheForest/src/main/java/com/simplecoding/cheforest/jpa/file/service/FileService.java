package com.simplecoding.cheforest.jpa.file.service;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import com.simplecoding.cheforest.jpa.file.dto.FileDto;
import com.simplecoding.cheforest.jpa.file.entity.File;
import com.simplecoding.cheforest.jpa.file.repository.FileRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;
    private final MapStruct mapStruct;
    private final EntityManager em; // Member 참조용

    // 외부 저장 경로 (application.properties: file.upload-dir=C:/cheforest/uploads)
    @Value("${file.upload-dir}")
    private String uploadDir;

    // ====== 공통 유틸 ======
    /** 확장자를 1회만 추출(.jpg.jpg 방지)하고, 저장 파일명을 UUID_원본이름+확장자 형태로 생성 */
    private String buildSavedName(String originalFilename) {
        String cleaned = StringUtils.cleanPath(Objects.requireNonNull(originalFilename));
        String base = cleaned;
        String ext = "";
        int dot = cleaned.lastIndexOf('.');
        if (dot != -1 && dot < cleaned.length() - 1) {
            ext = cleaned.substring(dot);         // ".jpg"
            base = cleaned.substring(0, dot);     // "image"
        }
        return UUID.randomUUID() + "_" + base + ext;
    }

    private Path ensureDir() throws IOException {
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        return dir;
    }

    /** OS 경로 → 웹 경로(/upload/**)로 변환하여 DB에 저장 */
    private String toWebPath(String savedName) {
        return "/uploads/" + savedName;
    }

    // ====== 단일 파일 저장 ======
    public FileDto saveFile(MultipartFile file,
                            String useType,
                            Long useTargetId,
                            String usePosition,
                            Long uploaderId) throws IOException {
        if (file == null || file.isEmpty()) return null;
        if (useTargetId == null) {
            throw new IllegalArgumentException(
                    "saveFile() called with null useTargetId. useType=" + useType + ", usePosition=" + usePosition
            );
        }
        String savedName = buildSavedName(file.getOriginalFilename());
        Path dir = ensureDir();
        Path target = dir.resolve(savedName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        Member uploader = em.getReference(Member.class, uploaderId);

        File fileEntity = File.builder()
                .fileName(savedName)                 // 실제 파일명만 저장
                .filePath(toWebPath(savedName))      // 웹 접근 경로(/upload/...)
                .fileType(getFileExtension(savedName))
                .useType(useType)
                .useTargetId(useTargetId)
                .usePosition(usePosition)            // "THUMBNAIL", "STEP_1", ...
                .uploader(uploader)
                .build();

        return mapStruct.toDto(fileRepository.save(fileEntity));
    }

    // ====== 여러 파일 저장 + 첫 번째 파일 ID 반환 (슬롯 저장 유지) ======
    public Long saveBoardFiles(Long boardId, Long uploaderId, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) return null;

        Long firstFileId = null;
        int idx = 0;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) { idx++; continue; }

            // 슬롯명 예: 대표/썸네일 등은 외부에서 saveFile 호출 시 usePosition으로 "THUMBNAIL" 넘기면 됨
            // 여기서는 단계 이미지 가정: STEP_1, STEP_2 ...
            String usePosition = "STEP_" + (idx + 1);
            FileDto dto = saveFile(file, "BOARD", boardId, usePosition, uploaderId);

            if (firstFileId == null && dto != null) {
                firstFileId = dto.getFileId();
            }
            idx++;
        }
        return firstFileId;
    }

    // ====== 파일 단건 조회 ======
    public FileDto getFile(Long fileId) {
        return fileRepository.findById(fileId)
                .map(mapStruct::toDto)
                .orElse(null);
    }

    // ====== 실제 파일 읽기 (필요시) ======
    public byte[] loadFileData(FileDto fileDto) throws IOException {
        if (fileDto == null) return null;
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path file = dir.resolve(fileDto.getFileName());
        if (!Files.exists(file)) return null;
        return Files.readAllBytes(file);
    }

    // ====== 게시글별 파일 목록 ======
    public List<FileDto> getFilesByBoardId(Long boardId) {
        return fileRepository.findByUseTypeAndUseTargetId("BOARD", boardId)
                .stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }

    // ====== 파일 삭제 (DB + 실제 파일) ======
    public void deleteFile(Long fileId) {
        fileRepository.findById(fileId).ifPresent(file -> {
            try {
                Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
                Path target = dir.resolve(file.getFileName());
                Files.deleteIfExists(target);
            } catch (IOException ignored) {}
            fileRepository.delete(file);
        });
    }

    // ====== 대상+타입 전체 삭제 ======
    public void deleteAllByTargetIdAndType(Long targetId, String useType) {
        List<File> list = fileRepository.findByUseTypeAndUseTargetId(useType, targetId);
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();

        for (File f : list) {
            try {
                Path target = dir.resolve(f.getFileName());
                Files.deleteIfExists(target);
            } catch (IOException ignored) {}
        }
        fileRepository.deleteByUseTargetIdAndUseType(targetId, useType);
    }

    // ====== 프로필 최신 파일 ======
    public FileDto getProfileFileByMemberId(Long memberId) {
        return fileRepository.findTop1ByUseTypeAndUseTargetIdOrderByInsertTimeDesc("MEMBER", memberId)
                .map(mapStruct::toDto)
                .orElse(null);
    }

    // ====== 도우미 ======
    public String getMimeType(String extension) {
        if (extension == null) return "application/octet-stream";
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }

    // ====== 확장자 추출 ======
    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int dot = fileName.lastIndexOf('.');
        if (dot == -1 || dot == fileName.length() - 1) return "";
        return fileName.substring(dot + 1);
    }
}
