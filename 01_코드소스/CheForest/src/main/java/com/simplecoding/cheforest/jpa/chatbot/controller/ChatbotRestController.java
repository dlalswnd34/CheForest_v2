package com.simplecoding.cheforest.jpa.chatbot.controller;

import com.simplecoding.cheforest.jpa.chatbot.dto.ChatbotFaqDto;
import com.simplecoding.cheforest.jpa.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatbotRestController {

    private final ChatbotService chatbotService;

    // 전체 조회
    @GetMapping("/api/chatbot/faq")
    public ResponseEntity<?> getAllFaqs() {
        try {
            List<ChatbotFaqDto> faqs = chatbotService.findAll();
            return ResponseEntity.ok(faqs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 카테고리별 조회
    @GetMapping("/api/chatbot/faq/category/{category}")
    public ResponseEntity<?> getFaqsByCategory(@PathVariable String category) {
        try {
            List<ChatbotFaqDto> faqs = chatbotService.findByCategory(category);
            return ResponseEntity.ok(faqs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 질문 검색
    @GetMapping("/api/chatbot/faq/search")
    public ResponseEntity<?> searchFaqs(@RequestParam String keyword) {
        try {
            List<ChatbotFaqDto> result = chatbotService.searchByQuestion(keyword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // FAQ 등록
    @PostMapping("/api/chatbot/faq")
    public ResponseEntity<?> createFaq(@RequestBody ChatbotFaqDto dto) {
        try {
            ChatbotFaqDto saved = chatbotService.save(dto);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // FAQ 단건 조회
    @GetMapping("/api/chatbot/faq/{id}")
    public ResponseEntity<?> getFaqById(@PathVariable Long id) {
        try {
            ChatbotFaqDto faq = chatbotService.findById(id);
            return ResponseEntity.ok(faq);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // FAQ 삭제
    @DeleteMapping("/api/chatbot/faq/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable Long id) {
        try {
            chatbotService.delete(id);
            return ResponseEntity.ok(Map.of("message", "삭제 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 사용자 질문 처리 (FAQ → GPT Fallback, POST 방식)
    @PostMapping("/api/chatbot/ask")
    public ResponseEntity<?> askChatbot(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            if (question == null || question.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "질문이 비어있습니다."));
            }

            String answer = chatbotService.getChatbotAnswer(question);
            return ResponseEntity.ok(Map.of("answer", answer));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 사용자 질문 처리 (GET - 테스트용)
    @GetMapping("/api/chatbot/ask")
    public ResponseEntity<?> askChatbotGet(@RequestParam String question) {
        try {
            String answer = chatbotService.getChatbotAnswer(question);
            return ResponseEntity.ok(Map.of("answer", answer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
