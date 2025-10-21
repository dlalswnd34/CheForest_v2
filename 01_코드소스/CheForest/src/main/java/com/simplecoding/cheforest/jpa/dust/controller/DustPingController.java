package com.simplecoding.cheforest.jpa.dust.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.cheforest.jpa.dust.dto.DustDto;
import com.simplecoding.cheforest.jpa.dust.entity.DustCache;
import com.simplecoding.cheforest.jpa.dust.repository.DustCacheRepository;
import com.simplecoding.cheforest.jpa.weather.dto.WeatherDto;
import com.simplecoding.cheforest.jpa.recipe.dto.RecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class DustPingController {

    @Value("${api.air.service-key}")
    private String serviceKey;

    @Value("${api.air.base-url}")
    private String baseUrl;

    private static final String[] SIDO_NAMES = {
            "서울","부산","대구","인천","광주","대전","울산","세종","경기","강원",
            "충북","충남","전북","전남","경북","경남","제주"
    };

    private final ObjectMapper om = new ObjectMapper();
    private final DustCacheRepository dustCacheRepository;

    // Flask 서버 호출 (AI 추천 레시피)
    private List<RecipeDto> fetchAiRecipes(String grade) {
        try {
            String url = "http://localhost:5000/recommend/ai?grade="
                    + URLEncoder.encode(grade, StandardCharsets.UTF_8);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RecipeDto[]> resp =
                    restTemplate.getForEntity(url, RecipeDto[].class);
            RecipeDto[] body = resp.getBody();
            return body != null ? Arrays.asList(body) : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 등급 변환
    private String grade(String val, boolean pm25) {
        if (val == null || val.isBlank() || "-".equals(val)) return "정보없음";
        try {
            int v = Integer.parseInt(val);
            if (pm25) return v <= 15 ? "좋음" : v <= 35 ? "보통" : v <= 75 ? "나쁨" : "매우나쁨";
            return v <= 30 ? "좋음" : v <= 80 ? "보통" : v <= 150 ? "나쁨" : "매우나쁨";
        } catch (Exception ignored) {
            return "정보없음";
        }
    }

    // 평균 계산
    private String avg(List<String> vals) {
        int sum = 0, n = 0;
        for (String s : vals) {
            if (s != null && !s.isBlank() && !"-".equals(s)) {
                try {
                    sum += Integer.parseInt(s);
                    n++;
                } catch (Exception ignored) {}
            }
        }
        return n == 0 ? "-" : String.valueOf(Math.round((double) sum / n));
    }

    // 전국 시도별 미세먼지 현황
    @GetMapping("/dust/today-all")
    public List<DustDto> getTodayAll() {
        List<DustDto> out = new ArrayList<>();
        for (String sido : SIDO_NAMES) {
            out.add(fetchOnce(sido));
        }
        return out;
    }

    // 특정 지역 정보 (미세먼지 + 날씨 + 추천 레시피)
    @GetMapping("/dust/info")
    public DustDto getRegionInfo(@RequestParam String sido) {
        DustDto dust = fetchOnce(sido);

        // 1) 날씨 (임시 값 or 별도 API 연동 가능)
        WeatherDto weather = new WeatherDto();
        weather.setRegion(sido);
        weather.setTemperature("21");
        weather.setSky("흐림");
        weather.setHumidity("80%");
        weather.setBaseDate("20250924");
        weather.setBaseTime("09:00");
        dust.setWeather(weather);

        // 2) Flask AI 추천 레시피
        List<RecipeDto> recipes = fetchAiRecipes(dust.getPm10Grade());
        dust.setRecipes(recipes);

        return dust;
    }

    // 실제 API 호출 (DB 캐시 연동)
    private DustDto fetchOnce(String sido) {
        String finalUrl = null;
        try {
            String encodedSido = URLEncoder.encode(sido, StandardCharsets.UTF_8);

            finalUrl = baseUrl
                    + "?serviceKey=" + serviceKey
                    + "&returnType=json"
                    + "&numOfRows=1000"
                    + "&pageNo=1"
                    + "&sidoName=" + encodedSido
                    + "&ver=1.0";

            URL url = new URL(finalUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int code = conn.getResponseCode();
            BufferedReader rd = (code >= 200 && code <= 300)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            String body = sb.toString().trim();

            if (body.isEmpty() || body.charAt(0) != '{') {
                return loadFromCacheOrDefault(sido, "Non-JSON");
            }

            JsonNode resp = om.readTree(body).path("response");
            String resultCode = resp.path("header").path("resultCode").asText("");
            String resultMsg  = resp.path("header").path("resultMsg").asText("");

            if (!"00".equals(resultCode)) {
                return loadFromCacheOrDefault(sido, resultMsg);
            }

            // 정상 응답 처리
            JsonNode items = resp.path("body").path("items");
            List<String> p10 = new ArrayList<>(), p25 = new ArrayList<>();
            String dataTime = "-";
            if (items.isArray() && items.size() > 0) {
                for (JsonNode it : items) {
                    p10.add(it.path("pm10Value").asText("-"));
                    p25.add(it.path("pm25Value").asText("-"));
                    if ("-".equals(dataTime)) dataTime = it.path("dataTime").asText("-");
                }
            }

            String pm10 = avg(p10), pm25 = avg(p25);
            String pm10Grade = grade(pm10, false);
            String pm25Grade = grade(pm25, true);

            // DB 캐시 저장
            DustCache cache = new DustCache();
            cache.setSido(sido);
            cache.setPm10(pm10);
            cache.setPm25(pm25);
            cache.setPm10G(pm10Grade);
            cache.setPm25G(pm25Grade);
            cache.setDataTime(dataTime);
            cache.setResultCode("OK");
            dustCacheRepository.save(cache);

            // DTO 반환
            DustDto dto = new DustDto();
            dto.setSido(sido);
            dto.setPm10(pm10);
            dto.setPm25(pm25);
            dto.setPm10Grade(pm10Grade);
            dto.setPm25Grade(pm25Grade);
            dto.setDataTime(dataTime);
            dto.setResultCode("00");
            dto.setResultMsg("성공");
            dto.setUrl(finalUrl);
            return dto;

        } catch (Exception e) {
            return loadFromCacheOrDefault(sido, e.getMessage());
        }
    }

    // 실패 시 캐시에서 불러오기
    private DustDto loadFromCacheOrDefault(String sido, String errMsg) {
        return dustCacheRepository.findById(sido)
                .map(cache -> {
                    DustDto dto = new DustDto();
                    dto.setSido(cache.getSido());
                    dto.setPm10(cache.getPm10());
                    dto.setPm25(cache.getPm25());
                    dto.setPm10Grade(cache.getPm10G());
                    dto.setPm25Grade(cache.getPm25G());
                    dto.setDataTime(cache.getDataTime());
                    dto.setResultCode("EX");
                    dto.setResultMsg("API 오류, 이전값 반환: " + errMsg);
                    return dto;
                })
                .orElseGet(() -> {
                    DustDto dto = new DustDto();
                    dto.setSido(sido);
                    dto.setPm10("-");
                    dto.setPm25("-");
                    dto.setPm10Grade("정보없음");
                    dto.setPm25Grade("정보없음");
                    dto.setDataTime("-");
                    dto.setResultCode("EX");
                    dto.setResultMsg("DB에도 값 없음: " + errMsg);
                    return dto;
                });
    }
}
