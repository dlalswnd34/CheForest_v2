package com.simplecoding.cheforest.jpa.chat.service;

import com.simplecoding.cheforest.jpa.chat.dto.ChatMessage;
import com.simplecoding.cheforest.jpa.chat.entity.Message;
import com.simplecoding.cheforest.jpa.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;

    // 메시지 DB 저장
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    // 메시지 DB 호출
    public List<ChatMessage> getAllMessages() {
        return messageRepository.findAllByOrderByMessageDateAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // entity -> dto 변환
    public ChatMessage convertToDto(Message message) {
        ChatMessage dto = new ChatMessage();
        dto.setSenderId(message.getSender().getMemberIdx());
        dto.setSender(message.getSender().getNickname());
        dto.setProfile(message.getSender().getProfile()); // 프로필 사진
        dto.setMessage(message.getContent());
        dto.setTime(message.getMessageDate());
        return dto;
    }
}
