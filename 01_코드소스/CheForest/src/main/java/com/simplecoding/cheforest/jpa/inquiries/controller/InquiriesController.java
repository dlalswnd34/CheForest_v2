package com.simplecoding.cheforest.jpa.inquiries.controller;

import com.simplecoding.cheforest.jpa.inquiries.dto.InquiryWithNicknameDto;
import com.simplecoding.cheforest.jpa.inquiries.entity.Inquiries;
import com.simplecoding.cheforest.jpa.inquiries.repository.InquiriesRepository;
import com.simplecoding.cheforest.jpa.inquiries.service.InquiriesService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j2
@RequiredArgsConstructor
@RestController
public class InquiriesController {

    private final InquiriesService inquiriesService;
    private final InquiriesRepository inquiriesRepository;


    @GetMapping("/api/inquiries")
    public Map<String, Object> getPagedInquiries(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<InquiryWithNicknameDto> pageResult = inquiriesService.findInquiryWithNickname(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", pageResult.getContent());       // 실제 문의 내역
        response.put("total", pageResult.getTotalElements()); // 전체 개수
        response.put("totalPages", pageResult.getTotalPages()); // 전체 페이지 수
        response.put("page", pageResult.getNumber() + 1);     // 현재 페이지 번호 (1-based)
        return response;
    }

    @GetMapping("/api/inquiries/countAllInquiries")
    public Long countAllInquiries() {
        return inquiriesService.countAllInquiries();
    }

    @GetMapping("/api/inquiries/countPendingInquiries")
    public Long countPendingInquiries() {
        return inquiriesService.countPendingInquiries();
    }

    @GetMapping("/api/inquiries/countAnsweredInquiries")
    public Long countAnsweredInquiries() {
        return inquiriesService.countAnsweredInquiries();
    }

    @GetMapping("/api/inquiries/countTodayInquiries")
    public Long countTodayInquiries() {
        return inquiriesService.countTodayInquiries();
    }

    @GetMapping("/api/inquiries/pending")
    public List<InquiryWithNicknameDto> findPendingInquiriesWithNickname() {
        return inquiriesService.findPendingInquiriesWithNickname();
    }

    @Getter
    @Setter
    public static class AnswerRequestDto {
        private Long inquiryId;
        private String answerContent;
    }

    @PostMapping("/inquiries/answer")
    public ResponseEntity<String> submitAnswer(@RequestBody AnswerRequestDto dto) {
        try {
            log.info("답변 요청 수신: {}", dto);

            Optional<Inquiries> optional = inquiriesRepository.findById(dto.getInquiryId());
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("문의 내역을 찾을 수 없습니다.");
            }

            Inquiries inquiry = optional.get();
            inquiry.setAnswerContent(dto.getAnswerContent());
            // 답변 완료 시 상태와 일시 업데이트
            inquiry.setAnswerStatus("답변완료");
            inquiry.setAnswerAt(new Date());

            inquiriesRepository.save(inquiry);

            return ResponseEntity.ok("답변 저장 완료");

        } catch (Exception e) {
            log.error("답변 저장 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }

    @Getter
    @Setter
    public static class InquiryRequestDto  {
        private Long memberIdx;
        private String subject;  // 제목
        private String message;  // 질문 내용
    }

    @PostMapping("/inquiries/ask")
    public ResponseEntity<String> submitAsk(@RequestBody InquiryRequestDto requestDto) {
        // 유효성 검사
        if (requestDto.getMemberIdx() == null ||
                requestDto.getSubject() == null || requestDto.getSubject().trim().isEmpty() ||
                requestDto.getMessage() == null || requestDto.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("모든 항목을 입력해주세요.");
        }

        // Inquiries 객체 생성
        Inquiries inquiry = new Inquiries();
        inquiry.setMemberIdx(requestDto.getMemberIdx());
        inquiry.setTitle(requestDto.getSubject());
        inquiry.setQuestionContent(requestDto.getMessage());
        inquiry.setCreatedAt(new Date());
        inquiry.setAnswerStatus("대기중"); // 초기값 지정
        // inquiry.setLikeCount(0L); // LIKE_COUNT 필드가 필수는 아닐 수 있으므로 제거 혹은 기본값 설정 확인 필요

        // 저장
        inquiriesRepository.save(inquiry);

        return ResponseEntity.ok().body("문의가 등록되었습니다.");
    }

    @GetMapping("/api/searchInquiries")
    public Page<InquiryWithNicknameDto> getInquiries(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return inquiriesService.searchInquiries(keyword, status, pageable);
    }

    //   FAQ로 등록 및 해제
    @PostMapping("/inquiries/FAQ")
    public ResponseEntity<String> submitFaq(@RequestBody Map<String, Object> payload) {
        try {
            Long inquiryId = Long.valueOf(payload.get("inquiryId").toString());
            log.info("FAQ 등록 및 해제 요청 수신: {}", inquiryId);

            Optional<Inquiries> optional = inquiriesRepository.findById(inquiryId);
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("문의 내역을 찾을 수 없습니다.");
            }

            Inquiries inquiry = optional.get();
            // isFaq 값 정제
            String isFaq = Optional.ofNullable(inquiry.getIsFaq())
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .orElse("N");

            // 값 토글
            if ("Y".equals(isFaq)) {
                inquiry.setIsFaq("N");
            } else {
                inquiry.setIsFaq("Y");
            }
            inquiriesRepository.save(inquiry);
            String resultMessage = inquiry.getIsFaq().equals("Y") ? "FAQ로 등록되었습니다." : "FAQ 등록이 해제되었습니다.";

            return ResponseEntity.ok(resultMessage);

        } catch (Exception e) {
            log.error("FAQ 등록 및 해제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }

    // 문의사항 삭제 (관리자용)
    @PostMapping("/inquiries/delete")
    public ResponseEntity<String> deleteInquiries(@RequestBody Map<String, Object> payload) {
        try {
            Long inquiryId = Long.valueOf(payload.get("inquiryId").toString());
            log.info("문의사항 삭제 요청 수신: {}", inquiryId);

            Optional<Inquiries> optional = inquiriesRepository.findById(inquiryId);
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("문의 내역을 찾을 수 없습니다.");
            }

            inquiriesRepository.deleteById(inquiryId);
            String resultMessage = "성공적으로 문의사항이 삭제되었습니다.";

            return ResponseEntity.ok(resultMessage);

        } catch (Exception e) {
            log.error("문의사항 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }
}
