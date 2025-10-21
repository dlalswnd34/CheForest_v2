package com.simplecoding.cheforest.jpa.chatbot.repository;

import com.simplecoding.cheforest.jpa.chatbot.entity.ChatbotFaq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatbotFaqRepository extends JpaRepository<ChatbotFaq, Long> {

    Optional<ChatbotFaq> findTopByQuestionContainingIgnoreCase(String keyword);

}
