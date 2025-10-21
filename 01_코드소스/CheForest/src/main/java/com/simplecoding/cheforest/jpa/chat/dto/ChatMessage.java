package com.simplecoding.cheforest.jpa.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {

    /**
     * 채팅 메시지의 유형을 정의합니다.
     * TEXT: 일반 텍스트 메시지
     * IMAGE: 이모티콘 메시지 (ChatStompController에서 검증에 사용)
     */
    public enum MessageType {
        TEXT, IMAGE
    }

    // 이 필드가 누락되어 ChatStompController에서 getType() 에러가 발생했습니다.
    private MessageType type;

    private Long senderId; // message 테이블에서 member 테이블로부터 fk로 발신자 식별 (식별용)
    private String sender; // 발신자 닉네임 (출력용)
    private String message; // message 테이블에 content (텍스트 또는 이모티콘 URL)

    private String profile; // 프로필 사진

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm") // 날짜 포멧 설정
    private LocalDateTime time ; // message 테이블에 message date
}
