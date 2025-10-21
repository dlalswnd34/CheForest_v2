package com.simplecoding.cheforest.jpa.footer.controller;

import com.simplecoding.cheforest.jpa.auth.service.EmailService;
import com.simplecoding.cheforest.jpa.footer.service.FooterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
@RestController
public class FooterController {

    private final FooterService footerService;
    private final EmailService emailService;

    @ModelAttribute("categoryTotals")
    public Map<String, Long> categoryTotals() {
        return footerService.getCategoryTotals();
    }

    @PostMapping("/api/footer/newsletter")
    public ResponseEntity<Map<String, Object>> subscribeNewsletter(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        // 이메일 형식 검증
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "msg", "올바른 이메일 주소를 입력해주세요."));
        }

        try {
            emailService.sendNewsletterMail(email); // EmailService의 새 메서드 재사용
            return ResponseEntity.ok(Map.of("ok", true, "msg", "CheForest 뉴스레터 구독 완료!"));
        } catch (Exception e) {
            log.error("뉴스레터 발송 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "msg", "메일 발송 중 오류가 발생했습니다."));
        }
    }
}