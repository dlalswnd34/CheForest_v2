package com.simplecoding.cheforest.jpa.admin.controller;

import com.simplecoding.cheforest.jpa.recipe.service.DataKoImportService;
import com.simplecoding.cheforest.jpa.recipe.service.SpoonacularImportService;
import com.simplecoding.cheforest.jpa.recipe.service.WfImportService;
import com.simplecoding.cheforest.jpa.recipe.service.progress.ImportMonitor;
import com.simplecoding.cheforest.jpa.recipe.service.progress.ImportProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RecipeImportController {

    private final SpoonacularImportService spoonacular;
    private final WfImportService wf;
    private final DataKoImportService datako;
    private final ImportMonitor monitor;

    private String nowFormatted() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    }

    // Spoonacular
    @PostMapping("/admin/import/spoonacular/run")
    public Map<String, Object> runSpoonacular() {
        String msg = spoonacular.run();
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "ok");
        res.put("source", "Spoonacular");
        res.put("message", msg);
        res.put("lastSync", nowFormatted());
        return res;
    }

    // WF(TheMeal)
    @PostMapping("/admin/import/wf/run")
    public Map<String, Object> runWf() {
        String msg = wf.run();
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "ok");
        res.put("source", "TheMealDB");
        res.put("message", msg);
        res.put("lastSync", nowFormatted());
        return res;
    }

    // DataKo(공공데이터포털)
    @PostMapping("/admin/import/datako/run")
    public Map<String, Object> runDataKo() {
        String msg = datako.run();
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "ok");
        res.put("source", "DataKO");
        res.put("message", msg);
        res.put("lastSync", nowFormatted());
        return res;
    }

    // 중지 요청
    @PostMapping("/admin/import/stop")
    public Map<String, Object> stopAll() {
        spoonacular.stop();
        wf.stop();
        datako.stop();
        monitor.stopAll();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "stopped");
        res.put("message", "중지 요청 완료");
        res.put("lastSync", nowFormatted());
        return res;
    }

    // 상태 조회
    @GetMapping("/admin/import/status/{task}")
    public Map<String, Object> getStatus(@PathVariable String task) {
        ImportProgress p = monitor.get(task);
        Map<String, Object> result = new LinkedHashMap<>();

        if (p == null) {
            result.put("status", "대기중");
            result.put("percent", 0);
            return result;
        }

        result.put("task", p.getTask());
        result.put("running", p.isRunning());
        result.put("processed", p.getProcessed().get());
        result.put("saved", p.getSaved().get());
        result.put("skipped", p.getSkipped().get());
        result.put("total", p.getTotal());
        result.put("percent", p.getPercent());
        result.put("stage", p.getStage());
        result.put("lastId", p.getLastId());
        result.put("error", p.getError());
        result.put("updatedAt", p.getUpdatedAt());

        return result;
    }
}
