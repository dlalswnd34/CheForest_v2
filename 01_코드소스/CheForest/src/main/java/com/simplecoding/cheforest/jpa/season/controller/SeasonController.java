package com.simplecoding.cheforest.jpa.season.controller;

import com.simplecoding.cheforest.jpa.season.entity.SeasonIngredient;
import com.simplecoding.cheforest.jpa.season.repository.SeasonIngredientRepository;
import com.simplecoding.cheforest.jpa.recipe.entity.Recipe;
import com.simplecoding.cheforest.jpa.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonIngredientRepository seasonIngredientRepository;
    private final RecipeRepository recipeRepository;

    @GetMapping("/season")
    public String showSeason(Model model) {
        List<SeasonIngredient> spring = seasonIngredientRepository.findBySeasons("spring");
        List<SeasonIngredient> summer = seasonIngredientRepository.findBySeasons("summer");
        List<SeasonIngredient> autumn = seasonIngredientRepository.findBySeasons("autumn");
        List<SeasonIngredient> winter = seasonIngredientRepository.findBySeasons("winter");

        // 순서 랜덤 섞기
        Collections.shuffle(spring);
        Collections.shuffle(summer);
        Collections.shuffle(autumn);
        Collections.shuffle(winter);

        model.addAttribute("springIngredients", spring);
        model.addAttribute("summerIngredients", summer);
        model.addAttribute("autumnIngredients", autumn);
        model.addAttribute("winterIngredients", winter);

        // 기본 탭 표시용
        model.addAttribute("season", "spring");

        log.info("loaded counts -> spring={}, summer={}, autumn={}, winter={}",
                spring.size(), summer.size(), autumn.size(), winter.size());

        return "season/seasonIngredient";
    }

    /**
     * 제철 재료 카드 버튼용 랜덤 레시피 추천.
     * 조건: API_RECIPE.season IS NOT NULL + title_kr LIKE %keyword% (대소문자 무시)
     * 동작: 후보 중 랜덤 1개 골라 상세조회로 리다이렉트
     * 예: /season/recommend?keyword=낙지
     */
    @GetMapping("/season/recommend")
    public String recommendRecipeAndRedirect(@RequestParam String keyword) {
        final String kw = (keyword == null) ? "" : keyword;

        List<Recipe> candidates =
                recipeRepository.findBySeasonIsNotNullAndTitleKrContainingIgnoreCase(kw);

        if (candidates == null || candidates.isEmpty()) {
            log.warn("추천 후보 없음: keyword='{}'", kw);
            // 필요 시 프로젝트 경로에 맞게 수정
            return "redirect:/recipe/list";
        }

        Recipe chosen = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        return "redirect:/recipe/view?recipeId=" + chosen.getRecipeId(); // PK 접근자명 프로젝트와 일치 확인
    }
}
