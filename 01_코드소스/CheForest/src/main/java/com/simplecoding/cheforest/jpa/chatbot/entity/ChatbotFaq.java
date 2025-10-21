package com.simplecoding.cheforest.jpa.chatbot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHATBOT_FAQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotFaq {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faq_seq")
    @SequenceGenerator(
            name = "faq_seq",
            sequenceName = "SEQ_CHATBOT_FAQ",
            allocationSize = 1
    )
    @Column(name = "FAQ_ID")
    private Long id;

    private String question;
    private String answer;
    private String category;
}
