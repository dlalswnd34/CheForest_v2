// src/main/java/com/simplecoding/cheforest/jpa/weather/scheduler/WeatherScheduler.java
package com.simplecoding.cheforest.jpa.weather.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.cheforest.jpa.weather.entity.WeatherCache;
import com.simplecoding.cheforest.jpa.weather.repository.WeatherCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherCacheRepository weatherCacheRepository;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${api.air.service-key}")
    private String serviceKey;

    @Value("${api.weather.base-url}")
    private String baseUrl;

    // 전국 주요 지역 좌표
    private static final Map<String, int[]> REGION_COORDS = Map.ofEntries(
            Map.entry("서울", new int[]{60, 127}),
            Map.entry("부산", new int[]{98, 76}),
            Map.entry("대구", new int[]{89, 90}),
            Map.entry("인천", new int[]{55, 124}),
            Map.entry("광주", new int[]{58, 74}),
            Map.entry("대전", new int[]{67, 100}),
            Map.entry("울산", new int[]{102, 84}),
            Map.entry("세종", new int[]{66, 103}),
            Map.entry("경기", new int[]{60, 120}),
            Map.entry("강원", new int[]{73, 134}),
            Map.entry("충북", new int[]{69, 107}),
            Map.entry("충남", new int[]{68, 100}),
            Map.entry("전북", new int[]{63, 89}),
            Map.entry("전남", new int[]{51, 67}),
            Map.entry("경북", new int[]{89, 91}),
            Map.entry("경남", new int[]{91, 77}),
            Map.entry("제주", new int[]{52, 38})
    );

    // 매 시각 30분마다 실행
    @Scheduled(cron = "0 10 * * * *")
    public void updateWeatherData() {
        for (Map.Entry<String, int[]> entry : REGION_COORDS.entrySet()) {
            String region = entry.getKey();
            int[] coords = entry.getValue();
            try {
                WeatherCache cache = callApi(region, coords[0], coords[1]);

                // PK(region) 기준으로 무조건 덮어쓰기
                weatherCacheRepository.save(cache);

                log.info("날씨 저장 완료: {} - {} {} {}",
                        region, cache.getTemperature(), cache.getHumidity(), cache.getSky());
            } catch (Exception e) {
                log.error("❌ {} 날씨 업데이트 실패: {}", region, e.getMessage());
            }
        }
    }

    // 기상청 API 호출
    private WeatherCache callApi(String region, int nx, int ny) throws Exception {
        // ====== 날짜/시간 보정 ======
        Calendar cal = Calendar.getInstance();
        String baseDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        int[] baseHours = {2, 5, 8, 11, 14, 17, 20, 23};
        int selectedHour = baseHours[0];
        for (int h : baseHours) {
            if (hour >= h) selectedHour = h;
        }
        if (hour < 2 || (hour == 2 && minute < 45)) {
            cal.add(Calendar.DATE, -1);
            baseDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
            selectedHour = 23;
        }
        String baseTime = String.format("%02d00", selectedHour);

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", "1000")
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);

        JsonNode items = om.readTree(response)
                .path("response").path("body").path("items").path("item");

        String temp = "-";
        String hum = "-";
        String sky = "-";

        if (items.isArray()) {
            for (JsonNode it : items) {
                String category = it.path("category").asText();
                String fcstValue = it.path("fcstValue").asText();

                switch (category) {
                    case "TMP": temp = fcstValue + "℃"; break;
                    case "REH": hum = fcstValue + "%"; break;
                    case "SKY":
                        if ("1".equals(fcstValue)) sky = "맑음";
                        else if ("3".equals(fcstValue)) sky = "구름많음";
                        else if ("4".equals(fcstValue)) sky = "흐림";
                        break;
                    case "PTY": // PTY가 SKY보다 우선
                        switch (fcstValue) {
                            case "1": sky = "비"; break;
                            case "2": sky = "비/눈"; break;
                            case "3": sky = "눈"; break;
                            case "5": sky = "빗방울"; break;
                            case "6": sky = "빗방울눈날림"; break;
                            case "7": sky = "눈날림"; break;
                        }
                        break;
                }
            }
        }

        return WeatherCache.builder()
                .region(region)
                .temperature(temp)
                .humidity(hum)
                .sky(sky)
                .baseDate(baseDate)
                .baseTime(baseTime)
                .dataTime(LocalDateTime.now())
                .resultCode("OK")
                .build();
    }
}
