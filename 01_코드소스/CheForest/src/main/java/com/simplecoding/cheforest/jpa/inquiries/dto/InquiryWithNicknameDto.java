package com.simplecoding.cheforest.jpa.inquiries.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InquiryWithNicknameDto {
    private Long inquiryId;
    private Long memberIdx;
    private String title;
    private String questionContent;
    private String answerContent;
    private String answerStatus;
    private String isFaq;
    private Date createdAt;
    private Date answerAt;
    private String nickname;
}
