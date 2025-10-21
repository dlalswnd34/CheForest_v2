package com.simplecoding.cheforest.jpa.dust.controller;

import com.simplecoding.cheforest.jpa.dust.entity.DustCache;
import com.simplecoding.cheforest.jpa.dust.repository.DustCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DustController {

    private final DustCacheRepository dustCacheRepository;

    /**
     * 특정 지역 미세먼지 조회 (DB 캐시 조회 전용)
     */
    @GetMapping("/dust")
    public DustCache getDust(@RequestParam String sido) {
        log.info("🌫 단일 조회용 미세먼지 요청: {}", sido);
        return dustCacheRepository.findById(sido)
                .orElseGet(() -> {
                    DustCache empty = new DustCache();
                    empty.setSido(sido);
                    empty.setPm10("-");
                    empty.setPm25("-");
                    empty.setPm10G("정보없음");
                    empty.setPm25G("정보없음");
                    empty.setDataTime("-");
                    empty.setResultCode("EX");
                    return empty;
                });
    }
}
