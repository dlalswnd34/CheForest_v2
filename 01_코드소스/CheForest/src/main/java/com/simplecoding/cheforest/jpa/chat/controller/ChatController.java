package com.simplecoding.cheforest.jpa.chat.controller;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.chat.dto.ChatMessage;
import com.simplecoding.cheforest.jpa.chat.entity.Message;
import com.simplecoding.cheforest.jpa.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    // 나중에 ViewController 하나 만들어서 클래스 단위로 분리.
    @GetMapping("/chat")
    public String chatPage() {
        return "chat/chat";
    }



    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // 초기 채팅 이력 로딩 (나중에 JS fetch나 AJAX로 호출 가능)
    @GetMapping("/chat/history")
    @ResponseBody
    public List<ChatMessage> getChatHistory() {
        return chatService.getAllMessages();
    }

    // ("/pub/message")
    @MessageMapping("/message")
    public void message(@Payload ChatMessage message,
//                        @AuthenticationPrincipal CustomUserDetails userDetails
                        @AuthenticationPrincipal(expression = "member") Member sender) {

//        // 1️⃣ 전달된 ID로 Member 조회
//        Long memberId = userDetails.getMemberIdx();
//        Member sender = chatService.getMemberById(memberId);

        if (sender == null) {
            // 소셜/일반 불문하고 Principal → member 추출 실패 시 보호
            return;
        }


        // 2️⃣ DTO → Entity
        Message entity = new Message();
        entity.setContent(message.getMessage());
        entity.setSender(sender); // FK로 Member 연결

        // 3️⃣ DB 저장
        Message saved = chatService.saveMessage(entity);

        // 4️⃣ Entity → DTO 변환
        ChatMessage dto = chatService.convertToDto(saved);

        // 5️⃣ 브로드캐스트 ("/sub/message")
        messagingTemplate.convertAndSend("/sub/message", dto);
    }
}