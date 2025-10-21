package com.simplecoding.cheforest.jpa.recipe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "API_RECIPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    private String recipeId;   // PK

    // 한글 정보
    private String titleKr;

    private String categoryKr;

    @Lob
    private String instructionKr;

    private String ingredientKr;

    private String measureKr;

    // 영어 정보
    private String titleEn;

    private String categoryEn;

    @Lob
    private String instructionEn;

    private String ingredientEn;

    private String measureEn;

    // 기타
    private String thumbnail;

    private String area;

    private Long likeCount;

    @Column(nullable = false)
    private Long viewCount = 0L;

    private String cookTime;
    private String difficulty;


    // 미세먼지 좋은 음식 여부 (Y/N)
    @Column(name = "DUST_GOOD", length = 1, nullable = false)
    private String dustGood = "N";

    // 제철재료 레시피 검색 컬럼
    @Column(name = "season")
    private String season; // spring, summer, autumn, winter

    // 조회수 1 증가 로직
    public void addViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        this.viewCount += 1;
    }
}
