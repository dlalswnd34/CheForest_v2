package com.simplecoding.cheforest.jpa.season.repository;

import com.simplecoding.cheforest.jpa.season.entity.SeasonIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeasonIngredientRepository extends JpaRepository<SeasonIngredient, Long> {

    // 계절별 식재료 조회
    List<SeasonIngredient> findBySeasons(String seasons);

}
