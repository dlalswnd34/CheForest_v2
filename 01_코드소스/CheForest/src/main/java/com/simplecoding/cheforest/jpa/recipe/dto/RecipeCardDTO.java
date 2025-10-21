package com.simplecoding.cheforest.jpa.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCardDTO {
    private String id;            // RECIPE_ID (VARCHAR2)
    private String title;         // TITLE_KR
    private String thumbnail;     // THUMBNAIL
    private String categoryName;  // CATEGORY_KR
    private String writerNickname;// API 데이터는 없으니 "CheForest" 고정(표시용)
    private String cookTime;     // COOK_TIME (분)
    private String difficulty;    // DIFFICULTY
    private Long viewCount;       // VIEW_COUNT
    private Long likeCount;       // LIKE_COUNT
}