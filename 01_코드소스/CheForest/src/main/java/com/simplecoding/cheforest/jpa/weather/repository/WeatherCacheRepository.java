package com.simplecoding.cheforest.jpa.weather.repository;

import com.simplecoding.cheforest.jpa.weather.entity.WeatherCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherCacheRepository extends JpaRepository<WeatherCache, String> {
}
