package com.simplecoding.cheforest.jpa.chat.controller;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.security.CustomOAuth2User;
import com.simplecoding.cheforest.jpa.auth.security.CustomUserDetails;
import com.simplecoding.cheforest.jpa.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List; // FULL_EMOJI_LIST를 위해 필요

@Slf4j
@Controller // STOMP 컨트롤러는 보통 @Controller를 사용합니다.
@RequiredArgsConstructor
public class ChatStompController {

    // STOMP 메시지를 발행(publish)하는 데 사용
    private final SimpMessageSendingOperations messagingTemplate;

    // 서버에 정의된 전체 이모티콘 목록 (ChatService 또는 별도 파일에서 가져와야 함)
    private static final List<String> FULL_EMOJI_LIST = List.of(
            "/emoji/goooood.png", "/emoji/고소해요.png", "/emoji/맛없어요.png", "/emoji/맛있어요.png",
            "/emoji/매워요.png", "/emoji/반가워요.png", "/emoji/부드러워요.png", "/emoji/불쇼에요.png",
            "/emoji/슬퍼요.png", "/emoji/싫은데요.png", "/emoji/싱거워요.png", "/emoji/아쉬운데요.png",
            "/emoji/요리해요.png", "/emoji/우와.png", "/emoji/우욱.png", "/emoji/잘가요.png",
            "/emoji/진짜좋은데요.png", "/emoji/짜요.png", "/emoji/추천해요.png", "/emoji/화이팅.png"
    );


    /**
     * 클라이언트가 "/pub/message"로 메시지를 발행하면 이 메서드가 처리합니다.
     */
    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessage message, Principal principal) {

        if (principal == null) {
            log.warn("❌ Principal is null - WebSocket session not authenticated");
            return;
        }

        Member sender = null;

        // Principal의 실제 타입에 따라 분기
        if (principal instanceof Authentication authentication) {
            Object p = authentication.getPrincipal();

            if (p instanceof CustomUserDetails userDetails) {
                sender = userDetails.getMember();
            } else if (p instanceof CustomOAuth2User oAuthUser) {
                sender = oAuthUser.getMember();
            } else {
                log.warn("⚠️ Unknown principal type: {}", p.getClass().getName());
                return;
            }
        }

        if (sender == null) {
            log.warn("❌ Sender is null - 인증되지 않은 사용자입니다.");
            return;
        }

        if (message.getType() == ChatMessage.MessageType.IMAGE) {
            String emoteUrl = message.getMessage();
            int maxAllowedCount = sender.getMaxEmoteCount(); // Member 엔티티의 메서드 호출

            // 이모티콘의 인덱스 확인
            int emoteIndex = FULL_EMOJI_LIST.indexOf(emoteUrl);

            // 검증 로직
            if (emoteIndex == -1 || emoteIndex >= maxAllowedCount) {
                // 알 수 없거나 해금되지 않은 이모티콘은 무시하고 전송을 차단합니다.
                log.warn("{}이(가) 해금되지 않은 이모티콘 사용 시도: {} (허용 개수: {})",
                        sender.getLoginId(), emoteUrl, maxAllowedCount);
                return;
            }
        }

        // 검증을 통과한 경우에만 메시지 발행 (구독자에게 전송)
        messagingTemplate.convertAndSend("/sub/message", message);

        log.info("💬 채팅 메시지 발송 성공 - From: {} ({}) / Type: {}",
                sender.getNickname(), sender.getMemberIdx(), message.getType());
    }
}