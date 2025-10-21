package com.simplecoding.cheforest.jpa.inquiries.service;

import com.simplecoding.cheforest.jpa.common.MapStruct;
import com.simplecoding.cheforest.jpa.inquiries.dto.InquiryWithNicknameDto;
import com.simplecoding.cheforest.jpa.inquiries.entity.Inquiries;
import com.simplecoding.cheforest.jpa.inquiries.repository.InquiriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiriesService {

    private final InquiriesRepository inquiriesRepository;

    public void save(Inquiries inquiries) {
        inquiriesRepository.save(inquiries);
    }

    // 전체 문의 리스트 조회
    public Page<InquiryWithNicknameDto> getPagedInquiryWithNicknameDto(int page) {
        Pageable pageable = PageRequest.of(page, 10); // 0부터 시작하는 페이지
        return inquiriesRepository.findInquiryWithNickname(pageable);
    }

    public Page<InquiryWithNicknameDto> findInquiryWithNickname(Pageable pageable) {
        return inquiriesRepository.findInquiryWithNickname(pageable);
    }

    // 전체 문의사항 데이터 수 카운트
    public Long countAllInquiries() {
        return inquiriesRepository.countAllInquiries();
    }

    // ANSWER_STATUS = '대기중' 인 데이터 수 카운트
    public Long countPendingInquiries() {
        return inquiriesRepository.countPendingInquiries();
    }

    // ANSWER_STATUS = '답변완료' 인 데이터 수 카운트
    public Long countAnsweredInquiries() {
        return inquiriesRepository.countAnsweredInquiries();
    }

    // 오늘 작성된 문의사항 수 카운트
    public Long countTodayInquiries() {
        return inquiriesRepository.countTodayInquiries();
    }

    // '대기중' 상태인 문의 전체 리스트 조회
    public List<InquiryWithNicknameDto> findPendingInquiriesWithNickname() {
        return inquiriesRepository.findPendingInquiriesWithNickname();
    }

    // 전체문의 사항중 검색어와 상태검색이 일치하는 것들만 조회
    public Page<InquiryWithNicknameDto> searchInquiries(String keyword, String status, Pageable pageable) {
        return inquiriesRepository.findByKeywordAndStatus(keyword, status, pageable);
    }

    /**
     * 마이페이지: 특정 회원의 문의 내역을 조회합니다. (페이징 적용)
     * @param memberIdx 현재 로그인된 회원의 고유 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 해당 회원의 문의 내역 DTO 페이지
     */
    public Page<InquiryWithNicknameDto> getMyInquiries(Long memberIdx, String status, Pageable pageable) {
        // status 기본값 보정
        if (status == null || status.isBlank()) status = "all";
        return inquiriesRepository.findMyInquiriesWithNicknameAndStatus(memberIdx, status, pageable);
    }

    /**
     * 사용자가 자신의 답변 대기 중인 문의를 수정합니다.
     * @param inquiryId 수정할 문의 ID
     * @param memberIdx 현재 로그인된 회원 ID (보안 검증용)
     * @param title 새로운 제목
     * @param questionContent 새로운 문의 내용
     * @throws SecurityException 권한이 없거나 답변 완료된 문의일 경우
     * @throws IllegalArgumentException 문의를 찾을 수 없을 경우
     */
    @Transactional
    public void updateInquiry(Long inquiryId, Long memberIdx, String title, String questionContent) {
        Inquiries inquiry = inquiriesRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다. ID: " + inquiryId));

        // 1) 소유권 및 보안 검증
        if (!inquiry.getMemberIdx().equals(memberIdx)) {
            throw new SecurityException("자신이 작성한 문의만 수정할 수 있습니다.");
        }

        // 2) 답변 상태 검증 ('N' = 답변대기일 때만 수정 가능)
        // DB 스키마에 따라 '대기중'에 해당하는 문자열 값으로 조정해야 합니다.
        if (inquiry.getAnswerStatus().equals("Y") || inquiry.getAnswerStatus().equals("답변완료")) {
            throw new SecurityException("이미 답변이 완료된 문의는 수정할 수 없습니다.");
        }

        // 3) 내용 업데이트 및 저장
        inquiry.setTitle(title);
        inquiry.setQuestionContent(questionContent);
        // Save는 Transactional 덕분에 자동 반영되지만 명시적으로 호출할 수도 있습니다.
        inquiriesRepository.save(inquiry);
        log.info("문의 수정 완료: ID={}, Member={}", inquiryId, memberIdx);
    }

    /**
     * 사용자가 자신의 답변 대기 중인 문의를 삭제합니다.
     * @param inquiryId 삭제할 문의 ID
     * @param memberIdx 현재 로그인된 회원 ID (보안 검증용)
     * @throws SecurityException 권한이 없거나 답변 완료된 문의일 경우
     * @throws IllegalArgumentException 문의를 찾을 수 없을 경우
     */
    @Transactional
    public void deleteInquiry(Long inquiryId, Long memberIdx) {
        Inquiries inquiry = inquiriesRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다. ID: " + inquiryId));

        // 1) 소유권 및 보안 검증
        if (!inquiry.getMemberIdx().equals(memberIdx)) {
            throw new SecurityException("자신이 작성한 문의만 삭제할 수 있습니다.");
        }

        // 2) 답변 상태 검증 ('N' = 답변대기일 때만 삭제 가능)
        // DB 스키마에 따라 '대기중'에 해당하는 문자열 값으로 조정해야 합니다.
        if (inquiry.getAnswerStatus().equals("Y") || inquiry.getAnswerStatus().equals("답변완료")) {
            throw new SecurityException("이미 답변이 완료된 문의는 삭제할 수 없습니다.");
        }

        // 3) 삭제
        inquiriesRepository.delete(inquiry);
        log.info("문의 삭제 완료: ID={}, Member={}", inquiryId, memberIdx);
    }
}
