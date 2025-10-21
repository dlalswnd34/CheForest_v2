package com.simplecoding.cheforest.jpa.weather.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeatherDto {
    private String region;       // 지역
    private String temperature;  // 기온
    private String humidity;     // 습도
    private String sky;          // 하늘 상태 (맑음, 구름많음, 흐림 등)
    private String baseDate;     // 기준 날짜
    private String baseTime;     // 기준 시간

    @Builder
    public WeatherDto(String region, String temperature, String humidity,
                      String sky, String baseDate, String baseTime) {
        this.region = region;
        this.temperature = temperature;
        this.humidity = humidity;
        this.sky = sky;
        this.baseDate = baseDate;
        this.baseTime = baseTime;
    }
}
