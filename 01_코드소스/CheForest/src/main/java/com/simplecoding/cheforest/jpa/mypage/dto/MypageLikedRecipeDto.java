package com.simplecoding.cheforest.jpa.mypage.dto;

import lombok.Getter;

@Getter
public class MypageLikedRecipeDto {

    private final String recipeId;
    private final String titleKr;
    private final String categoryKr;
    private final Long   likeCount;
    private final String thumbnail;
    private final String likeDateText;

    // JPQL new(...) 에서 쓰는 생성자 — 시그니처 정확히 6개
    public MypageLikedRecipeDto(
            String recipeId,
            String titleKr,
            String categoryKr,
            Long likeCount,
            String thumbnail,
            String likeDateText
    ) {
        this.recipeId = recipeId;
        this.titleKr = titleKr;
        this.categoryKr = categoryKr;
        this.likeCount = likeCount;
        this.thumbnail = thumbnail;
        this.likeDateText = likeDateText;
    }
}
