package com.simplecoding.cheforest.jpa.chatbot.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotFaqDto {
    private Long id;
    private String question;
    private String answer;
    private String category;
}
