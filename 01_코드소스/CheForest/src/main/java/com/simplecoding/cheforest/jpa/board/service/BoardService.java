package com.simplecoding.cheforest.jpa.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.cheforest.es.integratedSearch.repository.IntegratedSearchRepository;
import com.simplecoding.cheforest.jpa.auth.security.AuthUser;
import com.simplecoding.cheforest.jpa.board.dto.*;
import com.simplecoding.cheforest.jpa.board.entity.Board;
import com.simplecoding.cheforest.jpa.board.repository.BoardRepository;
import com.simplecoding.cheforest.jpa.board.repository.BoardRepositoryDsl;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import com.simplecoding.cheforest.jpa.common.util.JsonUtil;
import com.simplecoding.cheforest.jpa.common.util.StringUtil;
import com.simplecoding.cheforest.jpa.file.dto.FileDto;
import com.simplecoding.cheforest.jpa.file.service.FileService;
import com.simplecoding.cheforest.jpa.like.service.LikeService;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepositoryDsl boardRepositoryDsl;
    private final FileService fileService;
    private final LikeService likeService;
    private final MemberRepository memberRepository;
    private final MapStruct mapStruct;
    private final IntegratedSearchRepository integratedSearchRepository;
    private final ReviewService reviewService;

    // 목록 조회 (검색 + 페이징)
    @Transactional(readOnly = true)
    public Page<BoardListDto> searchBoards(String keyword, String category, String searchType, Pageable pageable) {
        Page<BoardListDto> result = boardRepositoryDsl.searchBoards(keyword, category, searchType, pageable);

        result.forEach(dto -> {
            if (dto.getInsertTime() != null) {
                dto.setCreatedAgo(toAgo(dto.getInsertTime()));
            }
        });
        return result;
    }

    // 상세 조회 (+ 조회수 증가)
    @Transactional
    public BoardDetailDto getBoardDetail(Long boardId) {
        BoardDetailDto dto = boardRepositoryDsl.findBoardDetail(boardId);
        if (dto == null) {
            throw new IllegalArgumentException("게시글 없음: " + boardId);
        }
        dto.setInsertTimeStr(com.simplecoding.cheforest.common.util.DateTimeUtil.format(dto.getInsertTime()));

        boardRepository.increaseViewCount(boardId);
        dto.setViewCount(dto.getViewCount() + 1);

        return dto;
    }

    // 게시글 등록
    @Transactional
    public Long create(BoardSaveReq dto, String writerEmail) throws IOException {
        Member writer = memberRepository.findByEmail(writerEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + writerEmail));

        Board board = mapStruct.toEntity(dto);
        board.setWriter(writer);
        board.setCookTime(dto.getCookTime());
        board.setPrepare(StringUtil.joinList(dto.getIngredientName()));
        board.setPrepareAmount(StringUtil.joinList(dto.getIngredientAmount()));

        List<StepDto> steps = new ArrayList<>();
        if (dto.getInstructionContent() != null) {
            for (int i = 0; i < dto.getInstructionContent().size(); i++) {
                String text = dto.getInstructionContent().get(i);

                steps.add(new StepDto(text, null));
            }
        }
        board.setContent(JsonUtil.toJson(steps));

        boardRepository.save(board);
        Long boardId = board.getBoardId();

        return boardId;
    }

    // 게시글 수정
    @Transactional
    public void update(Long boardId,
                       BoardSaveReq dto,
                       List<Long> deleteImageIds) throws IOException {

        // 로그인한 사용자 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthUser principal = (AuthUser) auth.getPrincipal();
        Member currentUser = principal.getMember();

        // 게시글 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));

        // 작성자 또는 관리자 권한 확인
        if (!board.getWriter().getEmail().equals(currentUser.getEmail()) &&
                currentUser.getRole() != Member.Role.ADMIN) {
            throw new SecurityException("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        // 기본 정보 업데이트
        board.setTitle(dto.getTitle());
        board.setCategory(dto.getCategory());
        board.setDifficulty(dto.getDifficulty());
        board.setCookTime(dto.getCookTime());

        // 재료 문자열 변환
        board.setPrepare(StringUtil.joinList(dto.getIngredientName()));
        board.setPrepareAmount(StringUtil.joinList(dto.getIngredientAmount()));

        // 기존 조리법 JSON → StepDto 변환 (LinkedHashMap 방지)
        List<StepDto> originalSteps = new ArrayList<>();
        if (board.getContent() != null && !board.getContent().isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> rawList =
                        mapper.readValue(board.getContent(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
                for (Map<String, Object> raw : rawList) {
                    StepDto step = new StepDto();
                    step.setText((String) raw.getOrDefault("text", ""));
                    step.setImage((String) raw.getOrDefault("image", null));
                    originalSteps.add(step);
                }
            } catch (Exception e) {
                originalSteps = new ArrayList<>();
            }
        }

        // 새 조리법 + 이미지 병합
        List<StepDto> newSteps = new ArrayList<>();
        List<String> contents = dto.getInstructionContent();
        List<MultipartFile> images = dto.getInstructionImage();

        if (contents != null) {
            for (int i = 0; i < contents.size(); i++) {
                String text = contents.get(i);
                MultipartFile imageFile = (images != null && i < images.size()) ? images.get(i) : null;
                String imageUrl = null;

                // 새 이미지 업로드
                if (imageFile != null && !imageFile.isEmpty()) {
                    // 기존 이미지 있으면 삭제
                    if (i < originalSteps.size() && originalSteps.get(i).getImage() != null) {
                        try {
                            String oldUrl = originalSteps.get(i).getImage();
                            Long oldFileId = Long.parseLong(oldUrl.substring(oldUrl.lastIndexOf('/') + 1));
                            fileService.deleteFile(oldFileId);
                        } catch (Exception ignored) {}
                    }

                    FileDto stepImage = fileService.saveFile(imageFile,
                            "BOARD_STEP", boardId, String.valueOf(i + 1), board.getWriter().getMemberIdx());
                    imageUrl = "/file/board/preview/" + stepImage.getFileId();
                }
                // 새 이미지가 없으면 기존 이미지 유지
                else if (i < originalSteps.size()) {
                    imageUrl = originalSteps.get(i).getImage();
                }

                newSteps.add(new StepDto(text, imageUrl));
            }
        }

        // JSON 저장
        board.setContent(JsonUtil.toJson(newSteps));

        // 삭제 요청된 기존 첨부 파일 삭제
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            deleteImageIds.forEach(fileService::deleteFile);
        }

        // 대표 이미지 교체 로직
        if (dto.getMainImage() != null && !dto.getMainImage().isEmpty()) {
            if (board.getThumbnail() != null && !board.getThumbnail().isEmpty()) {
                try {
                    String oldUrl = board.getThumbnail();
                    Long oldFileId = Long.parseLong(oldUrl.substring(oldUrl.lastIndexOf('/') + 1));
                    fileService.deleteFile(oldFileId);
                } catch (Exception ignored) {}
            }

            FileDto thumb = fileService.saveFile(dto.getMainImage(),
                    "BOARD", boardId, "THUMBNAIL", board.getWriter().getMemberIdx());
            if (thumb != null) {
                board.setThumbnail("/file/board/preview/" + thumb.getFileId());
            }
        }

        // JPA 더티체킹으로 자동 update 수행
    }

    // 게시글 삭제
    public void delete(Long boardId) {
        reviewService.deleteByBoardId(boardId);
        likeService.deleteAllByBoardId(boardId);
        fileService.deleteAllByTargetIdAndType(boardId, "BOARD");
        boardRepository.deleteById(boardId);
    }

    // 인기글 조회
    @Transactional(readOnly = true)
    public List<BoardListDto> getBestPosts() {
        List<BoardListDto> posts = boardRepositoryDsl.findBestPosts();
        posts.forEach(dto -> {
            if (dto.getInsertTime() != null) {
                dto.setCreatedAgo(toAgo(dto.getInsertTime()));
            }
        });
        return posts;
    }

    // 카테고리별 인기 게시글 조회
    @Transactional(readOnly = true)
    public List<BoardListDto> getBestPostsByCategory(String category) {
        List<BoardListDto> posts = boardRepositoryDsl.findBestPostsByCategory(category);
        posts.forEach(dto -> {
            if (dto.getInsertTime() != null) {
                dto.setCreatedAgo(toAgo(dto.getInsertTime()));
            }
        });
        return posts;
    }

    // 인기 게시글 (좋아요 수 기준)
    @Transactional(readOnly = true)
    public List<BoardListDto> findPopularBoards() {
        return boardRepository.findTop5ByOrderByLikeCountDesc()
                .stream()
                .map(board -> mapStruct.toListDto(board))
                .toList();
    }

    // 카테고리별 게시글 수
    public long getCountByCategory(String category) {
        return boardRepository.countByCategory(category);
    }

    // 썸네일 업데이트
    public void updateThumbnail(Long boardId, String thumbnailPath) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));
        board.setThumbnail(thumbnailPath);
    }

    // 총 게시글 수
    public long getTotalCount() {
        return boardRepository.count();
    }

    // 날짜표시 전처리
    private String toAgo(java.time.LocalDateTime created) {
        java.time.Duration duration = java.time.Duration.between(created, java.time.LocalDateTime.now());

        long minutes = duration.toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = minutes / 60;
        if (hours < 24) return hours + "시간 전";

        long days = hours / 24;
        if (days < 7) return days + "일 전";

        long weeks = days / 7;
        if (weeks < 5) return weeks + "주 전";

        long months = days / 30;
        if (months < 12) return months + "개월 전";

        long years = days / 365;
        return years + "년 전";
    }

    // 최근 3개 조회
    public List<Board> recentPosts() {
        return boardRepository.findTop3ByOrderByInsertTimeDesc();
    }

    // 게시글 검색
    public List<BoardListDto> searchBoards(String keyword) {
        return boardRepository.searchBoards(keyword, PageRequest.of(0, 5))
                .stream()
                .map(mapStruct::toListDto)
                .toList();
    }

    // 카테고리별 랜덤 게시글 1개
    public BoardListDto getRandomBoardByCategory(String category) {
        List<Board> candidates = boardRepository.findRandomBoardsByCategory(category, 5);

        if (candidates == null || candidates.isEmpty()) return null;

        Board randomBoard = candidates.get(new Random().nextInt(candidates.size()));
        return mapStruct.toListDto(randomBoard);
    }
}





