package com.simplecoding.cheforest.jpa.home.service;

import com.simplecoding.cheforest.jpa.board.dto.BoardLatestRowDTO;
import com.simplecoding.cheforest.jpa.board.repository.BoardRepositoryDsl;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import com.simplecoding.cheforest.jpa.recipe.dto.RecipeCardDTO;
import com.simplecoding.cheforest.jpa.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final RecipeRepository recipeRepository;
    private final BoardRepositoryDsl boardRepositoryDsl;
    private final MapStruct mapStruct;

    // 인기 레시피 TOP4
    public List<RecipeCardDTO> getPopularRecipes() {
        return recipeRepository
                .findTop4ByLikeCountGreaterThanOrderByLikeCountDescViewCountDescRecipeIdDesc(0L)
                .stream()
                .map(mapStruct::toCardDto)
                .toList();
    }

    // 카테고리별 레시피 TOP3
    public Map<String, List<RecipeCardDTO>> getCategoryTop3Recipes() {
        Map<String, List<RecipeCardDTO>> result = new HashMap<>();
        String[] categories = {"한식", "양식", "중식", "일식", "디저트"};

        for (String category : categories) {
            List<RecipeCardDTO> top3 = recipeRepository
                    .findTop3ByCategoryKrAndLikeCountGreaterThanOrderByLikeCountDescViewCountDescRecipeIdDesc(category, 0L)
                    .stream()
                    .map(mapStruct::toCardDto)
                    .toList();
            result.put(category, top3);
        }
        return result;
    }

    // 카테고리별 최신 게시글 (댓글수 포함)
    public Map<String, List<BoardLatestRowDTO>> getCategoryLatestBoards() {
        Map<String, List<BoardLatestRowDTO>> result = new HashMap<>();
        String[] categories = {"한식", "양식", "중식", "일식", "디저트"};

        ZoneId zone = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zone);

        for (String category : categories) {
            List<BoardLatestRowDTO> latest = boardRepositoryDsl.findLatestByCategory(category, 3);

            // insertTime → createdAgo 문자열로 변환
            latest.forEach(row -> {
                if (row.getInsertTime() != null) {
                    String ago = toAgo(row.getInsertTime().atZone(zone), now);
                    row.setCreatedAgo(ago); // ← DTO에 setter 필요
                }
            });
            result.put(category, latest);
        }
        return result;
    }

    // 날짜 전처리
    private String toAgo(ZonedDateTime created, ZonedDateTime now) {
        long minutes = Duration.between(created, now).toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        long hours = minutes / 60; if (hours < 24) return hours + "시간 전";
        long days = hours / 24; if (days < 7) return days + "일 전";
        long weeks = days / 7; if (weeks < 5) return weeks + "주 전";
        long months = days / 30; if (months < 12) return months + "개월 전";
        long years = days / 365; return years + "년 전";
    }
}
