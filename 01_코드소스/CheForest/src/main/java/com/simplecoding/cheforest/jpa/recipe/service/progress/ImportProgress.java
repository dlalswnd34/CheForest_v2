package com.simplecoding.cheforest.jpa.recipe.service.progress;

import lombok.Data;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ImportProgress {
    private String task;                 // "datako" | "spoonacular" | "wf"
    private volatile boolean running;    // 실행 중 여부
    private volatile int total;          // 전체 건수 (모르면 0)
    private final AtomicInteger processed = new AtomicInteger(0);
    private final AtomicInteger saved = new AtomicInteger(0);
    private final AtomicInteger skipped = new AtomicInteger(0);
    private volatile String stage = "";  // 현재 단계(예: "fetch", "parse", "save")
    private volatile String lastId = ""; // 마지막 처리한 ID
    private volatile String error;       // 오류 메시지
    private volatile Instant startedAt = Instant.now();
    private volatile Instant updatedAt = Instant.now();

    public int getPercent() {
        int t = total;
        int p = processed.get();

        // total이 아직 0인 경우에도 processed 값 기준으로 점진 표시
        if (t <= 0) {
            if (!running) return 100;         // 종료 상태면 100%
            if (p == 0) return 0;             // 아직 아무것도 안 했으면 0%
            if (p < 5) return p * 10;         // 1~4단계: 약간의 시각적 피드백
            return Math.min(95, 20 + (p % 50)); // 진행 중: 20~95% 구간에서 진동
        }

        // 정상 계산
        double percent = (p * 100.0) / t;
        if (!running && percent < 100) percent = 100; // 종료 시 강제 100%
        return Math.min(100, (int) Math.round(percent));
    }
}
