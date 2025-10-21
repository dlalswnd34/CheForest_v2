package com.simplecoding.cheforest.jpa.chatbot.service;

import com.simplecoding.cheforest.jpa.chatbot.dto.ChatbotFaqDto;
import com.simplecoding.cheforest.jpa.chatbot.entity.ChatbotFaq;
import com.simplecoding.cheforest.jpa.chatbot.repository.ChatbotFaqRepository;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotFaqRepository faqRepository;
    private final MapStruct mapStruct;
    private final OpenAiService openAiService;

    // FAQ 전체 조회
    public List<ChatbotFaqDto> findAll() {
        return faqRepository.findAll().stream()
                .map(mapStruct::toDto)
                .toList();
    }

    // 카테고리별 조회
    public List<ChatbotFaqDto> findByCategory(String category) {
        return faqRepository.findAll().stream()
                .filter(faq -> faq.getCategory() != null &&
                        faq.getCategory().equalsIgnoreCase(category))
                .map(mapStruct::toDto)
                .toList();
    }

    // 키워드 검색
    public List<ChatbotFaqDto> searchByQuestion(String keyword) {
        return faqRepository.findAll().stream()
                .filter(faq -> faq.getQuestion() != null &&
                        faq.getQuestion().contains(keyword))
                .map(mapStruct::toDto)
                .toList();
    }

    // FAQ 저장
    public ChatbotFaqDto save(ChatbotFaqDto dto) {
        ChatbotFaq entity = mapStruct.toEntity(dto);
        ChatbotFaq saved = faqRepository.save(entity);
        return mapStruct.toDto(saved);
    }

    // 단건 조회
    public ChatbotFaqDto findById(Long id) {
        ChatbotFaq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found: " + id));
        return mapStruct.toDto(faq);
    }

    // 삭제
    public void delete(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new RuntimeException("삭제할 FAQ가 존재하지 않습니다: " + id);
        }
        faqRepository.deleteById(id);
    }

    // 챗봇 로직 (FAQ → GPT Fallback)
    public String findAnswerFromFaq(String question) {
        return faqRepository.findTopByQuestionContainingIgnoreCase(question)
                .map(ChatbotFaq::getAnswer)
                .orElse(null);
    }

    // 챗봇 응답 로직
    public String getChatbotAnswer(String question) {
        String faqAnswer = findAnswerFromFaq(question);
        if (faqAnswer != null) return faqAnswer;
        return openAiService.askChefBot(question);
    }
}
