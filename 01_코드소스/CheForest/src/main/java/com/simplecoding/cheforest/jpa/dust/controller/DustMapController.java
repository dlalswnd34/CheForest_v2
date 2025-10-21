// src/main/java/com/simplecoding/cheforest/jpa/dust/controller/DustMapController.java
package com.simplecoding.cheforest.jpa.dust.controller;

import com.simplecoding.cheforest.jpa.dust.dto.DustDto;
import com.simplecoding.cheforest.jpa.dust.entity.DustCache;
import com.simplecoding.cheforest.jpa.dust.repository.DustCacheRepository;
import com.simplecoding.cheforest.jpa.recipe.dto.RecipeDto;
import com.simplecoding.cheforest.jpa.recipe.service.RecipeService;
import com.simplecoding.cheforest.jpa.weather.dto.WeatherDto;
import com.simplecoding.cheforest.jpa.weather.entity.WeatherCache;
import com.simplecoding.cheforest.jpa.weather.repository.WeatherCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DustMapController {

    private final DustCacheRepository dustCacheRepository;   // 미세먼지 캐시
    private final WeatherCacheRepository weatherCacheRepository; // 날씨 캐시
    private final RecipeService recipeService; // Java 서비스 (Fallback 용)
    private final RestTemplate restTemplate = new RestTemplate();

    // JSP 맵 열기
    @GetMapping("/dustmap")
    public String dustmap() {
        return "dust/dustmap";
    }

    // 지역별 미세먼지 + 날씨 + 레시피 추천
    @ResponseBody
    @GetMapping("/dust/with-recommend")
    public DustDto withRecommend(@RequestParam String sido) {
        // 1) Dust
        DustCache dCache = dustCacheRepository.findById(sido).orElse(null);
        DustDto dto = new DustDto();
        if (dCache != null) {
            dto.setSido(dCache.getSido());
            dto.setPm10(dCache.getPm10());
            dto.setPm10Grade(dCache.getPm10G());
            dto.setPm25(dCache.getPm25());
            dto.setPm25Grade(dCache.getPm25G());
            dto.setDataTime(dCache.getDataTime());
            dto.setResultCode(dCache.getResultCode());
            dto.setResultMsg("OK".equals(dCache.getResultCode()) ? "성공" : "캐시/실패");
        } else {
            dto.setSido(sido);
            dto.setPm10("-");
            dto.setPm25("-");
            dto.setPm10Grade("정보없음");
            dto.setPm25Grade("정보없음");
            dto.setDataTime("-");
            dto.setResultCode("EX");
            dto.setResultMsg("DB에 미세먼지 데이터 없음");
        }

        // 2) Weather
        WeatherCache wCache = weatherCacheRepository.findById(sido).orElse(null);
        if (wCache != null) {
            dto.setWeather(new WeatherDto(
                    wCache.getRegion(),
                    wCache.getTemperature(),
                    wCache.getHumidity(),
                    wCache.getSky(),
                    wCache.getBaseDate(),
                    wCache.getBaseTime()
            ));
        } else {
            dto.setWeather(new WeatherDto(
                    sido, "-", "-", "정보없음", "-", "-"
            ));
        }

        // 3) Recipes (Flask와 로직 동일하게)
        List<RecipeDto> recipes = Collections.emptyList();
        String grade = dto.getPm25Grade();

        try {
            if ("나쁨".equals(grade) || "매우나쁨".equals(grade)) {
                // 나쁨 / 매우나쁨 → dust_good (자바에서 제공)
                recipes = recipeService.getRandomDustGood(5);
            } else {
                // 좋음 / 보통 → Flask 서버에 위임
                String flaskUrl = "http://localhost:5000/recommend/ai?grade=" + grade;
                RecipeDto[] arr = restTemplate.getForObject(flaskUrl, RecipeDto[].class);
                recipes = (arr != null) ? Arrays.asList(arr) : Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("레시피 추천 오류", e);
            // Flask 실패 시 Fallback → 자바에서 랜덤 한식 5개
            recipes = recipeService.getRandomRecipesByCategory("한식", 5);
        }

        dto.setRecipes(recipes);
        return dto;
    }
}
