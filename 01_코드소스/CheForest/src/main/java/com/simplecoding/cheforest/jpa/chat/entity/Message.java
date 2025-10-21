package com.simplecoding.cheforest.jpa.chat.entity;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private String content;

    private LocalDateTime messageDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY) // (fetch = FetchType.LAZY) 쿼리최적화용
    @JoinColumn(name = "member_idx", nullable = false)
    private Member sender;
}
