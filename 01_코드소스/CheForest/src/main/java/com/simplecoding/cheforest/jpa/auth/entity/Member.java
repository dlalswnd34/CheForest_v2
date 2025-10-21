package com.simplecoding.cheforest.jpa.auth.entity;

import com.simplecoding.cheforest.jpa.chat.entity.Message;
import com.simplecoding.cheforest.jpa.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "MEMBER")
@SequenceGenerator(
        name = "MEMBER_SEQ_JPA",
        sequenceName = "MEMBER_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_JPA")
    private Long memberIdx;   // PK

    @Column(name = "ID", nullable = false, unique = true)
    private String loginId;  // 로그인 ID
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;        // USER / ADMIN
    private String nickname;
    private String profile;
    private String socialId;   // 카카오, 구글, 네이버 식별자
    private String provider;   // "KAKAO", "GOOGLE", "NAVER"
    private Long point = 0L;      // 누적 포인트
    private String grade = "씨앗"; // 회원 등급

    // made by yes_ung 09/25
    private Date lastLoginTime;
    private String suspension;

    public enum Role {
        USER, ADMIN, LEFT
    }

    // 채팅용
    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();

    /**
     * 현재 회원의 등급에 따라 허용되는 최대 이모티콘 개수를 반환합니다.
     * 이 값은 ChatStompController (서버 검증)와 UserApiController (프론트엔드 UI 검증)에 사용됩니다.
     */
    @Transient
    public int getMaxEmoteCount() {
        return switch (this.grade) {
            case "숲" -> 20;
            case "나무" -> 16;
            case "새싹" -> 12;
            case "뿌리" -> 8;
            case "씨앗" -> 4;
            default -> 4; // 기본값
        };
    }
}
