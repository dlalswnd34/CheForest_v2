package com.simplecoding.cheforest.jpa.recipe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.cheforest.jpa.recipe.entity.Recipe;
import com.simplecoding.cheforest.jpa.recipe.repository.RecipeRepository;
import com.simplecoding.cheforest.jpa.recipe.service.progress.ImportMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataKoImportService {

    private final RestTemplate restTemplate;
    private final ObjectMapper om = new ObjectMapper();
    private final RecipeRepository repo;
    private final ImportMonitor monitor;

    @Value("${datako.api.key}")
    private String serviceKey;

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Transactional
    public String run() {
        final String TASK = "datako";                 // 고유 태스크 이름
        if (!running.compareAndSet(false, true)) return "이미 실행 중입니다.";

        int saved = 0, skipped = 0;
        try {
            // 진행률 초기화
            monitor.start(TASK, 0);
            monitor.touch(TASK, "fetch", null);

            // API 호출
            String url = "http://openapi.foodsafetykorea.go.kr/api/"
                    + serviceKey + "/COOKRCP01/json/50/150";

            String json = restTemplate.getForObject(url, String.class);
            JsonNode rows = om.readTree(json).path("COOKRCP01").path("row");
            if (!rows.isArray()) return "API 데이터가 올바르지 않습니다.";

            int total = rows.size();
            monitor.start(TASK, total);              // 총 데이터 개수 설정

            // 데이터 저장 루프
            for (JsonNode r : rows) {
                if (!running.get()) break;

                String recipeId = "KO-" + safe(r, "RCP_SEQ");
                monitor.touch(TASK, "save", recipeId);   // 단계 갱신
                monitor.get(TASK).getProcessed().incrementAndGet();

                if (repo.existsById(recipeId)) {
                    skipped++;
                    monitor.get(TASK).getSkipped().incrementAndGet();
                    continue;
                }

                Recipe recipe = Recipe.builder()
                        .recipeId(recipeId)
                        .titleKr(safe(r, "RCP_NM"))
                        .instructionKr(joinManual(r))
                        .categoryKr(safe(r, "RCP_PAT2"))
                        .ingredientKr(safe(r, "RCP_PARTS_DTLS"))
                        .thumbnail(safe(r, "ATT_FILE_NO_MK"))
                        .area("Korean")
                        .likeCount(0L)
                        .viewCount(0L)
                        .dustGood("N")
                        .build();

                repo.save(recipe);
                saved++;
                monitor.get(TASK).getSaved().incrementAndGet();
            }

            // 완료 처리
            monitor.finish(TASK, null);
            log.info("[{}] 완료: saved={}, skipped={}", TASK, saved, skipped);
            return String.format("🥢 DataKO 완료: saved=%d, skipped=%d", saved, skipped);

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
        monitor.finish("datako", "사용자 중단");
    }

    private String safe(JsonNode n, String key) {
        String v = n.path(key).asText("");
        return (v == null || v.equals("null")) ? "" : v.trim();
    }

    private String joinManual(JsonNode n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 20; i++) {
            String step = safe(n, "MANUAL" + String.format("%02d", i));
            if (!step.isBlank()) {
                if (!sb.isEmpty()) sb.append("\n");
                sb.append(i).append(". ").append(step);
            }
        }
        return sb.toString();
    }
}
