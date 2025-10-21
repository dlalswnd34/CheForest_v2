package com.simplecoding.cheforest.jpa.recipe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.cheforest.jpa.common.util.Translator;
import com.simplecoding.cheforest.jpa.recipe.entity.Recipe;
import com.simplecoding.cheforest.jpa.recipe.repository.RecipeRepository;
import com.simplecoding.cheforest.jpa.recipe.service.progress.ImportMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpoonacularImportService {

    private final RestTemplate restTemplate;
    private final ObjectMapper om = new ObjectMapper();
    private final RecipeRepository repo;
    private final Translator translator;   // 제목/조리법/재료 번역에만 사용
    private final ImportMonitor monitor;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    // spoonacular는 cuisine 파라미터로 가져옵니다.
    private static final List<String> CUISINES = List.of("chinese", "japanese");
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Transactional
    public String run() {
        final String TASK = "spoonacular";
        if (!running.compareAndSet(false, true)) return "이미 실행 중입니다.";

        int saved = 0, skipped = 0, total = 0;
        try {
            monitor.start(TASK, 0);
            monitor.touch(TASK, "fetch", null);

            // 전체 예상 개수
            for (String cuisine : CUISINES) {
                String url = "https://api.spoonacular.com/recipes/complexSearch"
                        + "?number=30&addRecipeInformation=true"
                        + "&cuisine=" + cuisine
                        + "&apiKey=" + apiKey;

                String json = restTemplate.getForObject(url, String.class);
                JsonNode results = om.readTree(json).path("results");
                if (results.isArray()) total += results.size();
            }
            monitor.start(TASK, total);

            // 수집 및 저장
            for (String cuisine : CUISINES) {
                String url = "https://api.spoonacular.com/recipes/complexSearch"
                        + "?number=30&addRecipeInformation=true"
                        + "&cuisine=" + cuisine
                        + "&apiKey=" + apiKey;

                String json = restTemplate.getForObject(url, String.class);
                JsonNode results = om.readTree(json).path("results");
                if (!results.isArray()) continue;

                for (JsonNode n : results) {
                    if (!running.get()) break;

                    String recipeId = "SPN-" + n.path("id").asText();
                    monitor.touch(TASK, "save", recipeId);
                    monitor.get(TASK).getProcessed().incrementAndGet();

                    if (repo.existsById(recipeId)) {
                        skipped++;
                        monitor.get(TASK).getSkipped().incrementAndGet();
                        continue;
                    }

                    String titleEn       = n.path("title").asText("");
                    String instructionEn = extractInstruction(n);
                    String categoryEn    = cuisine; // spoonacular는 cuisine이 사실상 분류
                    String ingredientEn  = extractIngredientsCsv(n);
                    String thumbnail     = n.path("image").asText("");

                    // 번역(제목/조리법/재료만)
                    monitor.touch(TASK, "translate", recipeId);
                    String titleKr       = translator.translate(titleEn, "KO");
                    String instructionKr = translator.translate(instructionEn, "KO");
                    String ingredientKr  = translator.translate(ingredientEn, "KO");

                    // 카테고리는 규칙 매핑으로 고정 (번역 사용 X)
                    String categoryKr    = mapCategoryToKorean(categoryEn);

                    Recipe recipe = Recipe.builder()
                            .recipeId(recipeId)
                            .titleEn(titleEn).instructionEn(instructionEn).categoryEn(categoryEn)
                            .ingredientEn(ingredientEn)
                            .titleKr(titleKr).instructionKr(instructionKr).categoryKr(categoryKr)
                            .ingredientKr(ingredientKr)
                            .thumbnail(thumbnail)
                            .area(null)
                            .likeCount(0L)
                            .viewCount(0L)
                            .dustGood("N")
                            .build();

                    repo.save(recipe);
                    saved++;
                    monitor.get(TASK).getSaved().incrementAndGet();
                }
            }

            monitor.finish(TASK, null);
            log.info("[{}] 완료: saved={}, skipped={}", TASK, saved, skipped);
            return String.format("Spoonacular 완료: saved=%d, skipped=%d", saved, skipped);

        } catch (Exception e) {
            log.error("❌ [{}] import 실패", TASK, e);
            monitor.finish(TASK, e.getMessage());
            return "실패: " + e.getMessage();
        } finally {
            running.set(false);
        }
    }

    public void stop() {
        running.set(false);
        monitor.finish("spoonacular", "사용자 중단");
    }

    private String extractInstruction(JsonNode n) {
        String s = n.path("instructions").asText("");
        if (s == null || s.isBlank()) s = n.path("summary").asText("");
        return s;
    }

    private String extractIngredientsCsv(JsonNode n) {
        JsonNode arr = n.path("extendedIngredients");
        if (!arr.isArray()) return "";
        return StreamSupport.stream(arr.spliterator(), false)
                .map(x -> x.path("original").asText(""))
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(","));
    }

    /** CATEGORY_EN → CheForest 한글 카테고리 매핑 */
    private String mapCategoryToKorean(String categoryEn) {
        if (categoryEn == null) return "기타";
        String s = categoryEn.trim().toLowerCase();

        if (s.contains("korean"))   return "한식";
        if (s.contains("japanese")) return "일식";
        if (s.contains("chinese"))  return "중식";

        // 디저트 계열 키워드
        if (s.contains("dessert") || s.contains("sweet") || s.contains("bakery") ||
                s.contains("cake") || s.contains("cookie") || s.contains("pie"))
            return "디저트";

        // 나머지는 일단 '양식'으로 귀속 (TheMealDB 보정도 Wf에서 동일 룰 적용)
        return "양식";
    }
}
