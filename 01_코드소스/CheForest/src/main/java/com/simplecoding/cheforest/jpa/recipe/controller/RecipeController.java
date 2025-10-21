package com.simplecoding.cheforest.jpa.recipe.controller;

import com.simplecoding.cheforest.jpa.recipe.dto.RecipeDto;
import com.simplecoding.cheforest.jpa.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    // 이미지 캐싱 실행 (관리자 전용)
    @PostMapping("/recipe/cache-images")
    @ResponseBody
    public String cacheImages() {
        recipeService.downloadAndCacheAllImages();
        return "이미지 캐싱 완료!";
    }

    // 레시피 목록 조회 (카테고리 + 검색 + 검색타입 + 페이징 + 전체총합)
    @GetMapping("/recipe/list")
    public String showRecipeList(
            @RequestParam(defaultValue = "") String categoryKr,
            @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(defaultValue = "title") String searchType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // (필터 반영) 현재 페이지 데이터
        Page<RecipeDto> recipePage = recipeService.getRecipeList(categoryKr, searchKeyword, searchType, pageable);

        // 카테고리별 개수 맵  {"한식":77, "양식":87, ...}
        Map<String, Long> recipeCountMap = recipeService.getCategoryCounts();

        // 전체 총합(필터와 무관, ‘전체’ 뱃지에 쓰는 값)
        long allTotalCount = recipeService.countAllRecipes();

        model.addAttribute("recipeList", recipePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recipePage.getTotalPages());
        model.addAttribute("categoryKr", categoryKr);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("searchType", searchType);

        // 상단 “전체 레시피 n개”는 현재 필터/검색 기준
        model.addAttribute("totalCount", recipePage.getTotalElements());

        // 새로 추가
        model.addAttribute("allTotalCount", allTotalCount);
        model.addAttribute("recipeCountMap", recipeCountMap);

        // 기존
        model.addAttribute("best3Recipes", recipeService.getBest3Recipes());
        model.addAttribute("categoryList", recipeService.getAllCategories());

        return "recipe/recipelist";
    }

    // 카테고리별 레시피 수
    @GetMapping("/recipe/counts")
    @ResponseBody
    public Map<String, Long> getCategoryCountsApi() {
        return recipeService.getCategoryCounts();
    }

    // 레시피 상세 조회
    @GetMapping("/recipe/view")
    public String showRecipeDetail(@RequestParam String recipeId, Model model) {
        // 상세 들어올 때 조회수 +1
        recipeService.viewCount(recipeId);

        RecipeDto recipe = recipeService.getRecipeDetail(recipeId);
        model.addAttribute("recipe", recipe);
        return "recipe/recipeview";
    }

    // 레시피 JSON API (Flask에서 호출용)
    @GetMapping("/recipe/api/list")
    @ResponseBody
    public List<RecipeDto> getRecipeListApi(
            @RequestParam(defaultValue = "") String categoryKr,
            @RequestParam(required = false) String dustGood
    ) {
        if (dustGood != null && dustGood.equalsIgnoreCase("Y")) {
            // dust_good 전용 조회
            return recipeService.getRandomDustGood(50); // 최대 50개 가져오기
        }

        // searchType 파라미터가 없으므로 "title"을 기본값으로 하드코딩하여 전달
        return recipeService.getRecipeList(categoryKr, "", "title", PageRequest.of(0, 50)).getContent();
    }

    // 미세먼지에 좋은 음식 JSON API
    @GetMapping("/recipe/api/dust-good")
    @ResponseBody
    public List<RecipeDto> getDustGoodRecipes(
            @RequestParam(defaultValue = "5") int count // 기본 5개 가져오기
    ) {
        return recipeService.getRandomDustGood(count);
    }
}
