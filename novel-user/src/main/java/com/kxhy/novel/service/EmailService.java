package com.kxhy.novel.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface EmailService {

    /**
     * 生成验证码
     */
    String generateVerificationCode();

    /**
     * 存储验证码
     */
    void storeVerificationCode(String email, String code);

    /**
     * 验证验证码
     */
    boolean verifyCode(String email, String inputCode);

    /**
     * 异步发送验证码邮件
     */
    CompletableFuture<String> sendVerificationEmailAsync(String to, String verificationCode);

    /**
     * 获取验证码剩余时间
     */
    Long getVerificationCodeTTL(String email);

    /**
     * 获取服务状态
     */
    Map<String, Object> getPoolStatus();

    /**
     * 健康检查
     */
    boolean isHealthy();
}