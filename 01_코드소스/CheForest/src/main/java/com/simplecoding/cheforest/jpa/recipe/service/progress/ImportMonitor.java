package com.simplecoding.cheforest.jpa.recipe.service.progress;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImportMonitor {
    private final ConcurrentHashMap<String, ImportProgress> map = new ConcurrentHashMap<>();

    public ImportProgress start(String task, int total) {
        ImportProgress p = new ImportProgress();
        p.setTask(task);
        p.setRunning(true);
        p.setTotal(total);
        p.setStartedAt(Instant.now());
        p.setUpdatedAt(Instant.now());
        map.put(task, p);
        return p;
    }

    public ImportProgress get(String task) {
        return map.get(task);
    }

    public Map<String, ImportProgress> all() {
        return new HashMap<>(map);
    }

    public void touch(String task, String stage, String lastId) {
        ImportProgress p = map.get(task);
        if (p != null) {
            p.setStage(stage);
            if (lastId != null) p.setLastId(lastId);
            p.setUpdatedAt(Instant.now());
        }
    }

    public void finish(String task, String error) {
        ImportProgress p = map.get(task);
        if (p != null) {
            p.setRunning(false);
            p.setError(error);
            p.setUpdatedAt(Instant.now());
        }
    }

    public void stopAll() {
        map.values().forEach(p -> p.setRunning(false));
    }
}
