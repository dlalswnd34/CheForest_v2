package com.simplecoding.cheforest.jpa.admin.controller;


import com.simplecoding.cheforest.jpa.admin.dto.AccountStatusDTO;
import com.simplecoding.cheforest.jpa.admin.dto.CountTodayNewBoardDTO;
import com.simplecoding.cheforest.jpa.admin.dto.InquiriesIsFaqDto;
import com.simplecoding.cheforest.jpa.admin.dto.TodaySignUpUsersDto;
import com.simplecoding.cheforest.jpa.admin.service.AdminService;
import com.simplecoding.cheforest.jpa.auth.dto.MemberAdminDto;
import com.simplecoding.cheforest.jpa.auth.security.CustomUserDetails;
import com.simplecoding.cheforest.jpa.auth.service.MemberService;
import com.simplecoding.cheforest.jpa.board.dto.BoardListDto;
import com.simplecoding.cheforest.jpa.board.entity.Board;
import com.simplecoding.cheforest.jpa.board.service.BoardService;
import com.simplecoding.cheforest.jpa.recipe.dto.RecipeDto;
import com.simplecoding.cheforest.jpa.recipe.entity.Recipe;
import com.simplecoding.cheforest.jpa.recipe.repository.RecipeRepository;
import com.simplecoding.cheforest.jpa.recipe.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
// made by yes_ung
@RequiredArgsConstructor
@Log4j2
@Controller
public class AdminController {

    private final AdminService ytAdminService;
    private final MemberService MemberService;
    private final MemberService memberService;
    private final RecipeService recipeService;
    private final RecipeRepository  recipeRepository;
    private final BoardService boardService;

    //    관리자 페이지
    @GetMapping("/admin")
    public String mainTest(HttpServletRequest request, Model model) {
        request.getSession(true); // 세션이 없으면 새로 생성
//        총 가입자 수
        long allMemberCount = ytAdminService.getMemberCount();
//        금일 가입자 수
        long todayMemberCount = ytAdminService.getTodayMemberCount();
//        금일 가입자 정보
        List<TodaySignUpUsersDto>  todayMemberList = ytAdminService.getTodayMemberList();
//      활동,휴먼,제재 계정수 집계
        AccountStatusDTO accountStatusCounts = ytAdminService.getAccountStatusCounts();
//        금일 작성된 게시글,댓글 수
        CountTodayNewBoardDTO countTodayNewBoard = ytAdminService.getCountTodayNewBoard();
//        월별 작성된 게시글 및 가입자수 (통계용) json 자료형
        String monthlyActivityData = ytAdminService.getMonthlyActivityStats();
//        게시물 최근작성 3개
        List<Board> recentPosts = boardService.recentPosts();


        model.addAttribute("allMemberCount", allMemberCount);
        model.addAttribute("todayMemberCount", todayMemberCount);
        model.addAttribute("todayMemberList", todayMemberList);
        model.addAttribute("accountStatusCounts", accountStatusCounts);
        model.addAttribute("countTodayNewBoard", countTodayNewBoard);
        model.addAttribute("monthlyActivityData", monthlyActivityData);
        model.addAttribute("recentPosts", recentPosts);


        return "admin/admin";
    }
// 로그스테이시 실행 코드

    // 중복 실행 방지를 위한 상태 플래그
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @PostMapping("/api/logstash/start")
    public ResponseEntity<?> startLogstash() {
        // 이미 실행 중이면 요청 거부
        if (isRunning.get()) {
            return ResponseEntity.status(429).body(Map.of(
                    "message", "Logstash 작업이 이미 실행 중입니다."
            ));
        }

        isRunning.set(true); // 실행 중으로 표시

//        // 로컬 개발용
//        String logstashPath = "C:\\Users\\user\\Desktop\\opt\\logstash-8.14.3\\bin\\logstash.bat";
//        String confPath = "C:\\work\\elasticSearch\\integratedSearchLogstash.conf";

        // aws 배포용
        String logstashPath = "/usr/share/logstash/bin/logstash";
        String confPath = "/etc/logstash/conf.d/cheforest.conf";

        ProcessBuilder processBuilder = new ProcessBuilder(logstashPath, "-f", confPath);
        processBuilder.redirectErrorStream(true); // 에러 스트림을 출력 스트림에 통합

        try {
            Process process = processBuilder.start();

            // 로그 출력 읽기 (비동기)
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Logstash] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Logstash 종료까지 대기 (동기 방식)
            int exitCode = process.waitFor();
            System.out.println("Logstash 종료: " + exitCode);

            // 완료 시각 생성
            String lastSync = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm"));

            return ResponseEntity.ok(Map.of(
                    "message", "Logstash 작업이 완료되었습니다.",
                    "exitCode", exitCode,
                    "lastSync", lastSync        // 프런트가 이 값을 화면에 바로 반영
            ));
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()
            ));
        } finally {
            // 무조건 실행 상태 해제
            isRunning.set(false);
        }

    }


    @ResponseBody
    @GetMapping("/api/allMember")
    public Map<String, Object> getAllMember(@RequestParam(required = false) String keyword,
                                            @PageableDefault(size = 10, sort = "insertTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MemberAdminDto> pageResult = MemberService.adminAllMember(keyword,pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", pageResult.getContent());       // 실제 내용
        response.put("total", pageResult.getTotalElements()); // 전체 개수
        response.put("totalPages", pageResult.getTotalPages()); // 전체 페이지 수
        response.put("page", pageResult.getNumber() + 1);     // 현재 페이지 번호 (1-based)
        return response;
    }

    @ResponseBody
    @GetMapping("/api/suspendedMember")
    public Map<String, Object> getSuspendedMember(@RequestParam(required = false) String keyword,
                                                  @PageableDefault(size = 10, sort = "insertTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MemberAdminDto> pageResult = MemberService.adminSuspendedMember(keyword,pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", pageResult.getContent());       // 실제 내용
        response.put("total", pageResult.getTotalElements()); // 전체 개수
        response.put("totalPages", pageResult.getTotalPages()); // 전체 페이지 수
        response.put("page", pageResult.getNumber() + 1);     // 현재 페이지 번호 (1-based)
        return response;
    }
////    현재 세션의 내 로그인 정보 가져오기
    @ResponseBody
    @GetMapping("/api/member/me")
    public ResponseEntity<?> getCurrentMember(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "memberIdx", userDetails.getMemberIdx(),
                "username", userDetails.getUsername()
        ));
    }
// 추가 페이지 관리 qna
@GetMapping("/qna")
public String qna(Model model) {
    //        총 가입자 수
    long allMemberCount = ytAdminService.getMemberCount();
//    총 게시글 수
    long totalRecipeCount = ytAdminService.getTotalRecipeCount();
    //    자주 묻는 문의로 등록한 문의들
    List<InquiriesIsFaqDto> inquiriesIsFaq = ytAdminService.getInquiriesIsFaqDto();
   //    자주 묻는 문의로 등록한 문의수
    long inquiriesIsFaqCount = ytAdminService.getInquiriesIsFaqCount();

    model.addAttribute("allMemberCount", allMemberCount);
    model.addAttribute("totalRecipeCount", totalRecipeCount);
    model.addAttribute("inquiriesIsFaq", inquiriesIsFaq);
    model.addAttribute("inquiriesIsFaqCount", inquiriesIsFaqCount);
        return "support/qna";
}

    // 관리자페이지 회원 제재하기
    @ResponseBody
    @PostMapping("/admin/member/applySuspension")
    public ResponseEntity<String> applySuspensionMember(@RequestBody Map<String, Object> payload) {
//        try {
//            Long memberIdx = Long.valueOf(payload.get("memberIdx").toString());
//            log.info("회원 제재 요청 수신: {}", memberIdx);
//
//           memberService.applySuspension(memberIdx);
//
//            String resultMessage = "해당 회원에 대한 제재가 완료되었습니다.";
//            return ResponseEntity.ok(resultMessage);
//
//        } catch (Exception e) {
//            log.error("회원제재 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
//        }
        try {
            Long memberIdx = Long.valueOf(payload.get("memberIdx").toString());
            log.info("회원 제재/해제 요청 수신: {}", memberIdx);

            memberService.applySuspension(memberIdx);

            String resultMessage = "회원 상태가 성공적으로 변경되었습니다.";
            return ResponseEntity.ok(resultMessage);

        } catch (Exception e) {
            log.error("회원제재 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }
    // 관리자페이지 회원 삭제하기
    @ResponseBody
    @PostMapping("/admin/member/delete")
    public ResponseEntity<String> deleteMember(@RequestBody Map<String, Object> payload) {
        try {
            Long memberIdx = Long.valueOf(payload.get("memberIdx").toString());
            log.info("회원 삭제 요청 수신: {}", memberIdx);

            memberService.withdraw(memberIdx, null);  // 소셜로그인 회원탈퇴때문에 null추가

            String resultMessage = "해당 회원이 삭제되었습니다.";
            return ResponseEntity.ok(resultMessage);

        } catch (Exception e) {
            log.error("회원제재 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }
   //  관리자 페이지 레시피 조회
   @ResponseBody
   @GetMapping("/admin/getRecipes")
   public Map<String, Object> getRecipes(@RequestParam(defaultValue = "") String keyword,
                                         @RequestParam(defaultValue = "") String categoryKr,
                                         @RequestParam(defaultValue = "") String searchType,
                                         @PageableDefault(size = 10) Pageable pageable) {

       log.info("getRecipes() called with keyword={}, categoryKr={}, searchType={}, page={}",
               keyword, categoryKr, searchType, pageable.getPageNumber());

       try {
           Page<RecipeDto> pageResult = recipeService.getRecipeList(categoryKr, keyword, searchType, pageable);
           Map<String, Object> response = new HashMap<>();
           response.put("data", pageResult.getContent());
           response.put("total", pageResult.getTotalElements());
           response.put("totalPages", pageResult.getTotalPages());
           response.put("page", pageResult.getNumber() + 1);
           return response;

       } catch (Exception e) {
           log.error("레시피 목록 조회 중 예외 발생", e);
           throw e; // 또는 ResponseEntity로 감싸서 에러 메시지 반환
       }
   }
    // 관리자페이지 레시피 삭제
    @ResponseBody
    @PostMapping("/admin/deleteRecipes")
    public ResponseEntity<String> deleteInquiries(@RequestBody Map<String, Object> payload) {
        try {
            String recipeId = payload.get("recipeId").toString();
            log.info("레시피 삭제 요청 수신: {}", recipeId);

            Optional<Recipe> optional = recipeRepository.findById(recipeId);
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 레시피를 찾을 수 없습니다.");
            }

            recipeRepository.deleteById(recipeId);
            String resultMessage = "성공적으로 레시피가 삭제되었습니다.";

            return ResponseEntity.ok(resultMessage);

        } catch (Exception e) {
            log.error("FAQ 등록 및 해제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }
    //  관리자 페이지 게시물 조회
    @ResponseBody
    @GetMapping("/admin/getPosts")
    public Map<String, Object> getPosts(@RequestParam(defaultValue = "") String keyword,
                                          @RequestParam(defaultValue = "") String category,
                                          @RequestParam(defaultValue = "") String searchType,
                                          @PageableDefault(size = 10) Pageable pageable) {

        log.info("getRecipes() called with keyword={}, categoryKr={}, searchType={}, page={}",
                keyword, category, searchType, pageable.getPageNumber());

        try {
            Page<BoardListDto> pageResult = boardService.searchBoards(keyword, category, searchType, pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("data", pageResult.getContent());
            response.put("total", pageResult.getTotalElements());
            response.put("totalPages", pageResult.getTotalPages());
            response.put("page", pageResult.getNumber() + 1);
            return response;

        } catch (Exception e) {
            log.error("레시피 목록 조회 중 예외 발생", e);
            throw e; // 또는 ResponseEntity로 감싸서 에러 메시지 반환
        }
    }
    // 관리자페이지 게시물 삭제
    @ResponseBody
    @PostMapping("/admin/deletePost")
    public ResponseEntity<String> deleteInquiries1(@RequestBody Map<String, Object> payload) {
        try {
            Long boardId = Long.valueOf(payload.get("boardId").toString());
            log.info("게시물 삭제 요청 수신: {}", boardId);

            boardService.delete(boardId);

            String resultMessage = "성공적으로 레시피가 삭제되었습니다.";

            return ResponseEntity.ok(resultMessage);

        } catch (Exception e) {
            log.error("FAQ 등록 및 해제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }


}
