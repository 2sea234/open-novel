package com.kxhy.novel.service.impl;

import com.kxhy.novel.service.EmailService;
import com.kxhy.novel.config.EmailConfig;
import com.kxhy.novel.pool.EmailConnectionPool;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private EmailConnectionPool emailConnectionPool;

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis 操作对象
    private ValueOperations<String, String> redisOps;

    // 验证码相关常量
    private static final String VERIFICATION_CODE_PREFIX = "verify:code:";
    private static final String VERIFICATION_LIMIT_PREFIX = "verify:limit:";
    private static final Duration VERIFICATION_CODE_EXPIRE = Duration.ofMinutes(5);  // 5分钟过期
    private static final Duration VERIFICATION_LIMIT_EXPIRE = Duration.ofSeconds(60); // 60秒限制

    // 邮件发送线程池
    private final ThreadPoolExecutor emailExecutor;

    // 统计信息
    private final AtomicInteger sentCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private final AtomicLong totalSendTime = new AtomicLong(0);
    private final AtomicInteger verificationSuccessCount = new AtomicInteger(0);
    private final AtomicInteger verificationFailCount = new AtomicInteger(0);

    // 计时器
    private Timer emailSendTimer;

    // 重试执行器
    private final ScheduledExecutorService retryExecutor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r);
                t.setName("email-retry-scheduler");
                t.setDaemon(true);
                return t;
            });

    public EmailServiceImpl() {
        // 配置线程池
        int corePoolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;

        this.emailExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> {
                    Thread t = new Thread(r);
                    t.setName("email-sender-" + System.currentTimeMillis());
                    t.setDaemon(true);
                    return t;
                },
                (r, executor) -> {
                    log.warn("邮件发送任务队列已满，拒绝任务");
                    errorCount.incrementAndGet();
                    throw new RejectedExecutionException("邮件发送队列已满");
                }
        );

        // 允许核心线程超时回收
        this.emailExecutor.allowCoreThreadTimeOut(true);
    }

    @PostConstruct
    public void init() {
        // 初始化 Redis 操作对象
        this.redisOps = redisTemplate.opsForValue();

        // 初始化监控指标
        if (meterRegistry != null) {
            emailSendTimer = Timer.builder("email.send.time")
                    .description("邮件发送耗时")
                    .register(meterRegistry);

            meterRegistry.gauge("email.sent.count", sentCount);
            meterRegistry.gauge("email.error.count", errorCount);
            meterRegistry.gauge("email.verification.success", verificationSuccessCount);
            meterRegistry.gauge("email.verification.fail", verificationFailCount);
        }

        log.info("邮件服务初始化完成，线程池: {}/{}",
                emailExecutor.getPoolSize(), emailExecutor.getMaximumPoolSize());
    }

    /**
     * 生成验证码
     */
    public String generateVerificationCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

    /**
     * 存储验证码到 Redis
     */
    public void storeVerificationCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        try {
            redisOps.set(key, code, VERIFICATION_CODE_EXPIRE);
            log.debug("存储验证码成功: {} -> {}", email, code);
        } catch (Exception e) {
            log.error("存储验证码失败: {}, 错误: {}", email, e.getMessage());
            throw new RuntimeException("验证码存储失败", e);
        }
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String inputCode) {
        String key = VERIFICATION_CODE_PREFIX + email;
        try {
            String storedCode = redisOps.get(key);

            if (storedCode == null) {
                log.warn("验证码不存在或已过期: {}", email);
                verificationFailCount.incrementAndGet();
                return false;
            }

            boolean isValid = storedCode.equals(inputCode);

            if (isValid) {
                // 验证成功，删除验证码（一次性使用）
                redisTemplate.delete(key);
                verificationSuccessCount.incrementAndGet();
                log.info("验证码验证成功: {}", email);
            } else {
                verificationFailCount.incrementAndGet();
                log.warn("验证码验证失败: {}，输入: {}，存储: {}",
                        email, inputCode, storedCode);
            }

            return isValid;

        } catch (Exception e) {
            log.error("验证验证码失败: {}, 错误: {}", email, e.getMessage(), e);
            verificationFailCount.incrementAndGet();
            return false;
        }
    }

    /**
     * 检查发送频率限制
     */
    public boolean checkRateLimit(String email) {
        String limitKey = VERIFICATION_LIMIT_PREFIX + email;
        try {
            Long count = redisOps.increment(limitKey);

            if (count == 1) {
                // 第一次设置，设置过期时间
                redisTemplate.expire(limitKey, VERIFICATION_LIMIT_EXPIRE);
            }

            boolean allowed = count <= 3; // 60秒内最多3次
            if (!allowed) {
                log.warn("频率限制触发: {}，已发送次数: {}", email, count);
            }

            return allowed;

        } catch (Exception e) {
            log.error("检查频率限制失败: {}, 错误: {}", email, e.getMessage());
            return true; // 异常时允许发送
        }
    }

    /**
     * 发送验证邮件 - 核心方法
     */
    private String sendVerificationEmailSync(String to, String verificationCode) throws Exception {
        long startTime = System.nanoTime();
        SimpleEmail email = null;

        try {
            // 创建新实例（避免 MimeMessage 重用问题）
            email = createNewEmailInstance();

            // 设置邮件内容
            String fromEmail = emailConfig.getSmtp().getFromEmail();
            email.setFrom(fromEmail);
            email.addTo(to);
            email.setSubject("【小说平台】验证码");
            email.setMsg(buildEmailContent(verificationCode));

            // 发送邮件
            String result = email.send();
            sentCount.incrementAndGet();

            long duration = System.nanoTime() - startTime;
            totalSendTime.addAndGet(duration);

            log.debug("邮件发送成功 - 收件人: {}, 耗时: {}ms",
                    to, TimeUnit.NANOSECONDS.toMillis(duration));

            return result;

        } catch (Exception e) {
            errorCount.incrementAndGet();
            log.error("邮件发送失败 - 收件人: {}, 错误: {}", to, e.getMessage(), e);
            throw new EmailException("邮件发送失败: " + e.getMessage(), e);

        } finally {
            // 记录指标
            if (emailSendTimer != null) {
                emailSendTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            }
        }
    }

    /**
     * 构建邮件内容
     */
    private String buildEmailContent(String verificationCode) {
        return new StringBuilder()
                .append("亲爱的用户：\n\n")
                .append("您正在注册小说平台账号，验证码为：\n\n")
                .append("【").append(verificationCode).append("】\n\n")
                .append("验证码5分钟内有效，请勿泄露给他人。\n\n")
                .append("如非本人操作，请忽略此邮件。\n\n")
                .append("——小说平台团队")
                .toString();
    }

    /**
     * 创建新的邮件实例
     */
    private SimpleEmail createNewEmailInstance() throws EmailException {
        SimpleEmail email = new SimpleEmail();

        // 从配置获取参数
        EmailConfig.SmtpConfig smtp = emailConfig.getSmtp();

        email.setHostName(smtp.getHost());
        email.setSmtpPort(smtp.getPort());
        email.setSSLOnConnect(smtp.isSslEnabled());
        email.setStartTLSEnabled(true);
        email.setAuthentication(smtp.getFromEmail(), smtp.getAuthCode());
        email.setCharset("UTF-8");
        email.setSocketConnectionTimeout(smtp.getConnectionTimeout());
        email.setSocketTimeout(smtp.getTimeout());
        email.setDebug(false);

        return email;
    }

    /**
     * 异步发送验证码邮件（包含频率限制）
     */
    public CompletableFuture<String> sendVerificationEmailAsync(String to, String verificationCode) {
        CompletableFuture<String> future = new CompletableFuture<>();

        // 检查频率限制
        if (!checkRateLimit(to)) {
            future.completeExceptionally(
                    new RuntimeException("发送频率过高，请稍后再试")
            );
            return future;
        }

        emailExecutor.execute(() -> {
            String result = null;
            Exception lastException = null;

            // 重试逻辑
            EmailConfig.RetryConfig retryConfig = emailConfig.getRetry();
            int maxAttempts = retryConfig.getMaxAttempts();
            long backoffDelay = retryConfig.getBackoffDelay();
            double multiplier = retryConfig.getMultiplier();

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    result = sendVerificationEmailSync(to, verificationCode);
                    storeVerificationCode(to, verificationCode); // 存储验证码
                    future.complete(result);
                    return;

                } catch (Exception e) {
                    lastException = e;
                    retryCount.incrementAndGet();

                    if (attempt < maxAttempts) {
                        long delay = (long) (backoffDelay * Math.pow(multiplier, attempt - 1));
                        log.warn("邮件发送失败，第{}次重试，{}ms后重试 - 收件人: {}, 错误: {}",
                                attempt, delay, to, e.getMessage());

                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        log.error("邮件发送失败，已达最大重试次数{} - 收件人: {}, 错误: {}",
                                maxAttempts, to, e.getMessage(), e);
                    }
                }
            }

            future.completeExceptionally(lastException != null ?
                    lastException : new RuntimeException("邮件发送失败"));
        });

        // 设置超时
        ScheduledFuture<?> timeoutFuture = retryExecutor.schedule(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(
                        new TimeoutException("邮件发送超时 - 收件人: " + to)
                );
            }
        }, 30, TimeUnit.SECONDS);

        // 清理超时任务
        future.whenComplete((r, e) -> timeoutFuture.cancel(false));

        return future;
    }

    /**
     * 获取验证码剩余时间（秒）
     */
    public Long getVerificationCodeTTL(String email) {
        String key = VERIFICATION_CODE_PREFIX + email;
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null && ttl > 0 ? ttl : 0L;
        } catch (Exception e) {
            log.error("获取验证码剩余时间失败: {}, 错误: {}", email, e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取服务状态
     */
    public Map<String, Object> getPoolStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("threadPoolActive", emailExecutor.getActiveCount());
        status.put("threadPoolQueue", emailExecutor.getQueue().size());
        status.put("threadPoolSize", emailExecutor.getPoolSize());
        status.put("threadPoolMax", emailExecutor.getMaximumPoolSize());
        status.put("sentCount", sentCount.get());
        status.put("errorCount", errorCount.get());
        status.put("retryCount", retryCount.get());
        status.put("verificationSuccess", verificationSuccessCount.get());
        status.put("verificationFail", verificationFailCount.get());

        // 计算平均发送时间
        long avgSendTime = sentCount.get() > 0 ?
                totalSendTime.get() / (sentCount.get() * 1_000_000) : 0;
        status.put("avgSendTimeMs", avgSendTime);

        status.put("connectionPoolStats", emailConnectionPool.getPoolStats());

        return status;
    }

    /**
     * 健康检查
     */
    public boolean isHealthy() {
        try {
            // 测试 Redis 连接
            redisTemplate.hasKey("health-check");

            // 测试能否创建邮件实例
            SimpleEmail testEmail = createNewEmailInstance();

            // 检查线程池状态
            boolean threadPoolOk = !emailExecutor.isShutdown() &&
                    !emailExecutor.isTerminated() &&
                    emailExecutor.getActiveCount() < emailExecutor.getMaximumPoolSize() * 0.9;

            return threadPoolOk;

        } catch (Exception e) {
            log.warn("邮件服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 清理资源
     */
    @PreDestroy
    public void destroy() {
        try {
            // 关闭线程池
            emailExecutor.shutdown();
            if (!emailExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                emailExecutor.shutdownNow();
            }

            // 关闭重试执行器
            retryExecutor.shutdown();
            if (!retryExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                retryExecutor.shutdownNow();
            }

            log.info("邮件服务资源已清理，线程池状态: {}", emailExecutor.isTerminated());

        } catch (Exception e) {
            log.error("关闭邮件服务资源失败: {}", e.getMessage(), e);
        }
    }

}
