package com.simplecoding.cheforest.jpa.auth.dto;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 포함한 생성자 (from 메서드에 필요)
public class UserInfoDto {

    private String nickname;
    private String grade;
    private int maxEmoteCount; // 최종적으로 프론트엔드에 전달할 값

    /**
     * Member 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     * @param member 변환할 Member 엔티티
     * @return UserInfoDto 객체
     */
    public static UserInfoDto from(Member member) {
        return new UserInfoDto(
                member.getNickname(),
                member.getGrade(),
                member.getMaxEmoteCount() // Member 엔티티에 추가한 계산 메서드 호출
        );
    }
}
