package com.simplecoding.cheforest.jpa.recipe.controller;

import com.simplecoding.cheforest.jpa.recipe.dto.RecipeDto;
import com.simplecoding.cheforest.jpa.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class RecipeRestController {

    private final RecipeService recipeService;

    // 인기 레시피 Top 5
    @GetMapping("/api/recipes/popular")
    public List<RecipeDto> getPopularRecipes() {
        return recipeService.findPopularRecipes();
    }

    // 레시피 검색
    @GetMapping("/api/recipes/search")
    public List<RecipeDto> searchRecipes(@RequestParam String keyword) {
        return recipeService.searchRecipes(keyword);
    }

    // 카테고리별 랜덤 레시피 1개
    @GetMapping("/api/recipes/random")
    public RecipeDto getRandomRecipe(@RequestParam String category) {
        List<RecipeDto> candidates = recipeService.getRandomRecipesByCategory(category, 5);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        return candidates.get(new Random().nextInt(candidates.size())); // 무작위 1개 반환
    }
}
