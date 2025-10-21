package com.simplecoding.cheforest.jpa.board.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.cheforest.es.integratedSearch.entity.IntegratedSearch;
import com.simplecoding.cheforest.es.integratedSearch.service.IntegratedSearchService;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.auth.security.AuthUser;
import com.simplecoding.cheforest.jpa.auth.security.CustomOAuth2User;
import com.simplecoding.cheforest.jpa.auth.security.CustomUserDetails;
import com.simplecoding.cheforest.jpa.board.dto.*;
import com.simplecoding.cheforest.jpa.board.service.BoardService;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import com.simplecoding.cheforest.jpa.file.dto.FileDto;
import com.simplecoding.cheforest.jpa.file.service.FileService;
import com.simplecoding.cheforest.jpa.auth.dto.MemberDetailDto;
import com.simplecoding.cheforest.jpa.mypage.service.MypageService;
import com.simplecoding.cheforest.jpa.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * BoardController - 목록/상세/등록/수정/삭제 + 파일URL 주입(썸네일/조리법)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final FileService fileService;
    private final MypageService mypageService;
    private final MemberRepository memberRepository;
    private final PointService pointService;
    private final IntegratedSearchService integratedSearchService;
    private final MapStruct mapStruct;

    /**
     * FileDto -> 브라우저에서 접근 가능한 공개 URL로 변환
     * (미리보기 inline 엔드포인트 사용)
     */
    private String toPublicUrl(FileDto f) {
        if (f == null) return null;
        return "/file/board/preview/" + f.getFileId();
    }

    // 1. 목록 조회
    @GetMapping("/board/list")
    public String list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchType,
            @PageableDefault(size = 9, sort = "insertTime", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal AuthUser authUser,
            Model model
    ) {
        log.info("👉 category='{}', keyword='{}', searchType='{}'", category, keyword, searchType);

        // 게시글 목록
        Page<BoardListDto> boards = boardService.searchBoards(keyword, category, searchType, pageable);
        model.addAttribute("boards", boards.getContent());
        model.addAttribute("pageInfo", boards);

        // 인기글
        List<BoardListDto> bestPosts = (category == null || category.isBlank())
                ? boardService.getBestPosts()
                : boardService.getBestPostsByCategory(category);
        model.addAttribute("bestPosts", bestPosts);

        // ✅ 로그인 사용자 (일반 + 소셜 공통)
        Member me = (authUser != null) ? authUser.getMember() : null;
        if (me != null && me.getMemberIdx() != null) {
            Long memberIdx = me.getMemberIdx();

            model.addAttribute("myPostsTotalCount",       mypageService.getMyPostsCount(memberIdx, null));
            model.addAttribute("likedPostsTotalCount",    mypageService.getLikedBoardsCount(memberIdx, null));
            model.addAttribute("receivedLikesTotalCount", mypageService.getReceivedBoardLikes(memberIdx));
            model.addAttribute("myCommentsTotalCount",    mypageService.getMyCommentsCount(memberIdx, null));
        }

        return "board/boardlist";
    }

    // 2. 글 작성 폼
    @GetMapping("/board/add")
    public String showAddForm(Model model) {
        model.addAttribute("board", new BoardSaveReq());
        return "board/boardwrite";
    }

    // 3. 글 등록
    @PostMapping("/board/add")
    public String add(
            @ModelAttribute BoardSaveReq dto,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "instructionImage", required = false) List<MultipartFile> steps
    ) throws IOException {

        // ✅ 로그인 사용자 확인 (일반 + 소셜 모두 처리)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        Long memberIdx = null;
        String email = null;
        Member member = null;

        if (principal instanceof CustomUserDetails user) {
            member = user.getMember();
            memberIdx = member.getMemberIdx();
            email = member.getEmail();
        } else if (principal instanceof CustomOAuth2User social) {
            member = social.getMember();
            memberIdx = member.getMemberIdx();
            email = member.getEmail();
        } else {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 1️⃣ 게시글 저장
        Long boardId = boardService.create(dto, email);

        // 2️⃣ 썸네일 저장
        if (thumbnail != null && !thumbnail.isEmpty()) {
            FileDto thumb = fileService.saveFile(thumbnail, "BOARD", boardId, "THUMBNAIL", memberIdx);
            if (thumb != null) {
                String publicThumbUrl = toPublicUrl(thumb);
                boardService.updateThumbnail(boardId, publicThumbUrl);
            }
        }

        // 3️⃣ 단계 이미지 저장
        if (steps != null && !steps.isEmpty()) {
            fileService.saveBoardFiles(boardId, memberIdx, steps);
        }

        // E/S 넘기기
        IntegratedSearch searchDoc = mapStruct.boardToEntity(dto);
        searchDoc.setId(boardId.toString());
        searchDoc.setCooktime(dto.getCookTime());
        integratedSearchService.saveData(searchDoc);

        // 4️⃣ 포인트 적립
        pointService.addPointWithLimit(member, "POST");

        // 5️⃣ 목록으로 리다이렉트
        String encodedCategory = URLEncoder.encode(
                dto.getCategory() == null ? "" : dto.getCategory(),
                StandardCharsets.UTF_8
        );
        return "redirect:/board/list?category=" + encodedCategory;
    }

    // 4. 수정 페이지
    @GetMapping("/board/edition")
    public String editForm(@RequestParam("boardId") Long boardId, Model model) {

        // 1️⃣ 게시글 상세 조회
        BoardDetailDto board = boardService.getBoardDetail(boardId);
        if (board == null) {
            return "redirect:/board/list";
        }
        model.addAttribute("board", board);

        // 2️⃣ 첨부파일 목록
        List<FileDto> fileList = fileService.getFilesByBoardId(boardId);
        model.addAttribute("fileList", fileList);

        // 3️⃣ 재료 세팅
        List<Map<String, String>> ingredients = new ArrayList<>();
        if (board.getPrepare() != null && !board.getPrepare().isBlank() && board.getPrepareAmount() != null) {
            String[] names = board.getPrepare().split(",");
            String[] amounts = board.getPrepareAmount().split(",");
            for (int i = 0; i < names.length; i++) {
                Map<String, String> ing = new HashMap<>();
                ing.put("name", names[i].trim());
                ing.put("amount", (i < amounts.length ? amounts[i].trim() : ""));
                ingredients.add(ing);
            }
        }
        model.addAttribute("ingredients", ingredients);

        // 4️⃣ 조리법(JSON) 파싱
        List<StepDto> instructions = new ArrayList<>();
        if (board.getContent() != null && !board.getContent().isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();

                // ✅ LinkedHashMap으로 먼저 읽고 안전하게 StepDto로 변환
                List<Map<String, Object>> rawList =
                        mapper.readValue(board.getContent(), new TypeReference<List<Map<String, Object>>>() {});

                for (Map<String, Object> raw : rawList) {
                    StepDto step = new StepDto();
                    step.setText((String) raw.getOrDefault("text", ""));   // ✅ text 필드 유지
                    step.setImage((String) raw.getOrDefault("image", null));
                    instructions.add(step);
                }

            } catch (Exception e) {
                log.error("❌ 조리법 JSON 파싱 실패 (수정페이지): {}", e.getMessage());
            }
        }

        // 5️⃣ 단계별 이미지 URL 매핑
        List<FileDto> files = fileService.getFilesByBoardId(boardId);
        for (int i = 0; i < instructions.size(); i++) {
            int stepNo = i + 1;
            String stepImageUrl = files.stream()
                    .filter(f -> ("STEP_" + stepNo).equalsIgnoreCase(f.getUsePosition()))
                    .findFirst()
                    .map(this::toPublicUrl)
                    .orElse(instructions.get(i).getImage()); // 기존 JSON 이미지 유지
            instructions.get(i).setImage(stepImageUrl);
        }

        model.addAttribute("instructions", instructions);
        return "board/boardedit";
    }

    @PostMapping("/board/edit")
    public String update(
            @ModelAttribute BoardSaveReq dto,
            @RequestParam("boardId") Long boardId,
            @RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds
    ) throws IOException {

        // 로그인 안 되어 있으면 예외 처리
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 수정 수행 (Service 내부에서 작성자/관리자 판별)
        boardService.update(boardId, dto, deleteImageIds);

        // 통합검색(ES) 동기화
        IntegratedSearch searchDoc = mapStruct.boardToEntity(dto);
        searchDoc.setId(boardId.toString());
        integratedSearchService.saveData(searchDoc);

        // 리다이렉트
        return "redirect:/board/view?boardId=" + boardId;
    }

    // 6. 글 삭제
    @PostMapping("/board/delete")
    public String delete(@RequestParam("boardId") Long boardId) {
        boardService.delete(boardId);
        integratedSearchService.deleteData(boardId.toString());
        return "redirect:/board/list";
    }

//    7. 마이페이지용 글 삭제
    @DeleteMapping("/api/boards/{boardId}")
    public ResponseEntity<?> deleteBoardApi(@PathVariable Long boardId) {
        try {
            // 핵심: 기존에 사용하던 삭제 서비스를 그대로 재사용합니다.
            boardService.delete(boardId);

            // 성공 시, 자바스크립트에게 성공했다는 의미로 JSON 메시지를 보냅니다.
            return ResponseEntity.ok().body(Map.of("message", "게시글이 성공적으로 삭제되었습니다."));

        } catch (Exception e) {
            // 삭제 중 문제가 발생하면, 자바스크립트에게 실패했다는 의미로 에러 메시지를 보냅니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "삭제 처리 중 오류가 발생했습니다."));
        }
    }

    // 8. 상세 조회 (썸네일 + 조리법 이미지 URL 주입)
    @GetMapping("/board/view")
    public String view(@RequestParam("boardId") Long boardId, Model model,
                       @AuthenticationPrincipal MemberDetailDto loginUser) throws Exception {
        BoardDetailDto board = boardService.getBoardDetail(boardId);

        // 조리법 JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        List<StepDto> instructions = new ArrayList<>();

        if (board.getContent() != null && !board.getContent().isBlank()) {
            try {
                Object raw = mapper.readValue(board.getContent(), Object.class);
                instructions = mapper.convertValue(raw, new TypeReference<List<StepDto>>() {});
            } catch (Exception e) {
                log.warn("조리법 JSON 파싱 실패: {}", e.getMessage());
            }
        }

        // 파일 목록 조회
        List<FileDto> files = fileService.getFilesByBoardId(boardId);

        // 썸네일 URL (THUMBNAIL 우선, 없으면 board.thumbnail 사용)
        String thumbnailPath = files.stream()
                .filter(f -> "THUMBNAIL".equalsIgnoreCase(f.getUsePosition()))
                .findFirst()
                .map(this::toPublicUrl)     // ★ 공개 URL 변환
                .orElse(board.getThumbnail());

        // 단계 이미지 URL을 instructions[i].image에 주입 (STEP_1, STEP_2, ...)
        for (int i = 0; i < instructions.size(); i++) {
            final int stepNo = i + 1;
            String stepUrl = files.stream()
                    .filter(f -> ("STEP_" + stepNo).equalsIgnoreCase(f.getUsePosition()))
                    .findFirst()
                    .map(this::toPublicUrl) // ★ 공개 URL 변환
                    .orElse(null);
            if (stepUrl != null) {
                instructions.get(i).setImage(stepUrl);
            }
        }

        model.addAttribute("board", board);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("instructions", instructions);
        model.addAttribute("thumbnailPath", thumbnailPath);

        boolean isOwner = (loginUser != null)
                && (loginUser.getNickname() != null)
                && loginUser.getNickname().equals(board.getNickname());
        model.addAttribute("isOwner", isOwner);

        return "board/boardview";
    }

    // 카테고리별 총 게시글
    @GetMapping("/board/counts")
    @ResponseBody
    public Map<String, Long> getBoardCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("all", boardService.getTotalCount());
        counts.put("한식", boardService.getCountByCategory("한식"));
        counts.put("양식", boardService.getCountByCategory("양식"));
        counts.put("중식", boardService.getCountByCategory("중식"));
        counts.put("일식", boardService.getCountByCategory("일식"));
        counts.put("디저트", boardService.getCountByCategory("디저트"));
        return counts;
    }

}
