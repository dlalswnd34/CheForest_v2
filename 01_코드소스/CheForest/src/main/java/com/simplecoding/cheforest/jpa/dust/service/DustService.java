package com.simplecoding.cheforest.jpa.dust.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.cheforest.jpa.dust.entity.DustCache;
import com.simplecoding.cheforest.jpa.dust.repository.DustCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class DustService {

    @Value("${api.air.service-key}")
    private String serviceKey;

    @Value("${api.air.base-url}")
    private String baseUrl;

    private final DustCacheRepository dustCacheRepository;
    private final ObjectMapper om = new ObjectMapper();

    // 특정 시/도 미세먼지 조회 (API 호출 → DB 저장)
    public DustCache getDustInfo(String sido) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString(baseUrl)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("returnType", "json")
                    .queryParam("numOfRows", "1")
                    .queryParam("pageNo", "1")
                    .queryParam("sidoName", sido) // 자동 인코딩
                    .queryParam("ver", "1.0")
                    .build(true) // 반드시 true → 자동 인코딩
                    .toUri();

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = om.readTree(response).path("response");
            JsonNode item = root.path("body").path("items").get(0);

            if (item == null) {
                log.warn("❌ Dust API 응답에 item 없음: {}", sido);
                return errorCache(sido);
            }

            DustCache cache = new DustCache();
            cache.setSido(sido);
            cache.setPm10(item.path("pm10Value").asText("-"));
            cache.setPm25(item.path("pm25Value").asText("-"));
            cache.setPm10G(grade(cache.getPm10(), false));
            cache.setPm25G(grade(cache.getPm25(), true));
            cache.setDataTime(item.path("dataTime").asText("-"));
            cache.setResultCode("OK");

            // DB 저장 (덮어쓰기)
            dustCacheRepository.save(cache);

            return cache;

        } catch (Exception e) {
            log.error("❌ Dust API 호출 오류: {}", e.getMessage());
            return errorCache(sido);
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

    // 실패 시 더미 캐시
    private DustCache errorCache(String sido) {
        DustCache cache = new DustCache();
        cache.setSido(sido);
        cache.setPm10("-");
        cache.setPm25("-");
        cache.setPm10G("정보없음");
        cache.setPm25G("정보없음");
        cache.setDataTime("-");
        cache.setResultCode("EX");
        return cache;
    }
}
