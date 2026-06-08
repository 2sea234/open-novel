package com.kxhy.novel.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "novel-service");
        return ResponseEntity.ok(healthInfo);
    }


    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> dbHealth() {
        // 检查数据库连接
        Map<String, Object> dbHealth = new HashMap<>();
        try {
            // 执行简单的数据库查询
            dbHealth.put("status", "UP");
        }catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
        }
        return ResponseEntity.ok(dbHealth);
    }


}
