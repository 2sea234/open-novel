package com.kxhy.novel.controller;

import com.kxhy.novel.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
@Tag(name = "邮件服务", description = "邮箱相关接口")
@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-verification")
    @Operation(summary = "发送验证码", description = "注册用户时给用户的邮箱发送验证码")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String email = extractAndValidateEmail(request);

            // 生成验证码
            String code = emailService.generateVerificationCode();

            // 存储验证码（到Redis/数据库）
            emailService.storeVerificationCode(email, code);

            // 异步发送邮件
            emailService.sendVerificationEmailAsync(email, code)
                    .orTimeout(30, TimeUnit.SECONDS) // 设置超时
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            log.error("异步邮件发送失败 - 邮箱: {}, 错误: {}",
                                    email, throwable.getMessage());
                        } else {
                            log.info("异步邮件发送成功 - 邮箱: {}", email);
                        }
                    });

            // 立即返回响应
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "验证码发送中",
                    "email", maskEmail(email),
                    "timestamp", System.currentTimeMillis(),
                    "serviceStatus", emailService.getPoolStatus()
            ));

        } catch (IllegalArgumentException e) {
            log.warn("请求参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "code", "INVALID_PARAMETER"
            ));

        } catch (Exception e) {
            log.error("发送验证码失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "系统繁忙，请稍后重试",
                    "code", "SERVER_ERROR"
            ));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查邮件服务状态")
    public ResponseEntity<?> healthCheck() {
        try {
            boolean healthy = emailService.isHealthy();
            Map<String, Object> status = emailService.getPoolStatus();

            Map<String, Object> response = Map.of(
                    "healthy", healthy,
                    "status", healthy ? "UP" : "DOWN",
                    "timestamp", System.currentTimeMillis(),
                    "metrics", status
            );

            return healthy ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.status(503).body(response);

        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage(), e);
            return ResponseEntity.status(503).body(Map.of(
                    "healthy", false,
                    "status", "DOWN",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/metrics")
    @Operation(summary = "获取指标", description = "获取邮件服务性能指标")
    public ResponseEntity<?> getMetrics() {
        return ResponseEntity.ok(emailService.getPoolStatus());
    }

    /**
     * 提取并验证邮箱
     */
    private String extractAndValidateEmail(Map<String, String> request) {
        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("请求参数不能为空");
        }

        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        email = email.trim().toLowerCase();

        // 简单的邮箱格式验证
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }

        // 频率限制检查（可在此处添加）
        // checkRateLimit(email);

        return email;
    }

    /**
     * 邮箱格式验证
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.length() > 254) {
            return false;
        }

        // 简单的正则验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * 脱敏显示邮箱
     */
    private String maskEmail(String email) {
        if (email == null || email.length() < 5) {
            return "***";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }

        String prefix = email.substring(0, 2);
        String domain = email.substring(atIndex);
        return prefix + "***" + domain;
    }

    /**
     * DTO类用于验证请求
     */
    public static class SendEmailRequest {
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        // getter 和 setter
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}