package com.simplecoding.cheforest.jpa.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient webClient;

    public OpenAiService(@Value("${OPENAI_API_KEY}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String askChefBot(String userMsg) {
        Map<String, Object> request = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system",
                                "content", "너는 요리 정보 제공 사이트의 전용 셰프봇이야. "
                                        + "유저가 인사하면 '안녕하세요 세프봇입니다'로 대답해. "
                                        + "레시피, 조리법, 식재료, 음식문화 같은 요리 관련 질문만 대답해. "
                                        + "다른 주제는 '저는 요리 관련 정보만 안내해드릴 수 있어요 🍳' 라고 답해."),
                        Map.of("role", "user", "content", userMsg)
                )
        );

        Map response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // 응답 파싱
        List choices = (List) response.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");
            return message.get("content").toString();
        }
        return "죄송합니다, 답변을 가져오지 못했습니다.";
    }
}



