package com.simplecoding.cheforest.jpa.weather.controller;

import com.simplecoding.cheforest.jpa.weather.entity.WeatherCache;
import com.simplecoding.cheforest.jpa.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    // 전국 날씨 조회 (DB 캐시)
    @GetMapping("/weather/today/all")
    public List<WeatherCache> getAllWeather() {
        return weatherService.findAll();
    }

    // 특정 지역 날씨 조회 (DB 캐시)
    @GetMapping("/weather/today")
    public WeatherCache getWeather(@RequestParam(defaultValue = "서울") String region) {
        return weatherService.findByRegion(region);
    }
}
