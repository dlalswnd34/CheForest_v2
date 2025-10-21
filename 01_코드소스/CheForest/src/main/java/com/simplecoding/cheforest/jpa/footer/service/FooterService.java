package com.simplecoding.cheforest.jpa.footer.service;

import com.simplecoding.cheforest.jpa.board.repository.BoardRepository;
import com.simplecoding.cheforest.jpa.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FooterService {

    private final RecipeRepository recipeRepository;
    private final BoardRepository boardRepository;

    private Map<String, Long> cachedTotals;
    private LocalDateTime lastUpdated;

    public synchronized Map<String, Long> getCategoryTotals() {
        // 10분 캐시
        if (cachedTotals != null && lastUpdated != null &&
                lastUpdated.isAfter(LocalDateTime.now().minusMinutes(10))) {
            return cachedTotals;
        }

        Map<String, Long> totals = new LinkedHashMap<>();
        List<String> mainCategories = List.of("한식", "양식", "중식", "일식", "디저트");
        for (String c : mainCategories) totals.put(c, 0L);

        for (Object[] row : recipeRepository.countRecipesByCategory()) {
            String cat = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            if (mainCategories.contains(cat)) totals.merge(cat, count, Long::sum);
        }

        for (Object[] row : boardRepository.countBoardsByCategory()) {
            String cat = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            if (mainCategories.contains(cat)) totals.merge(cat, count, Long::sum);
        }

        cachedTotals = totals;
        lastUpdated = LocalDateTime.now();
        return cachedTotals;
    }
}