package com.kxhy.novel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.email")
public class EmailConfig {

    private SmtpConfig smtp;
    private PoolConfig pool;
    private RetryConfig retry;
    private TemplateConfig template;
    private MonitorConfig monitor;


    @Data
    public static class SmtpConfig {
        private String host;
        private int port;
        private boolean sslEnabled;
        private String fromEmail;
        private String authCode;
        private int connectionTimeout;
        private int timeout;
    }

    @Data
    public static class PoolConfig {
        private int maxTotal;
        private int maxIdle;
        private int minIdle;
        private int maxWaitMillis;
        private boolean testOnBorrow;
        private boolean testWhileIdle;
    }

    @Data
    public static class RetryConfig {
        private int maxAttempts;
        private long backoffDelay;
        private double multiplier;
    }

    @Data
    public static class TemplateConfig {
        private String verificationCode;
    }

    @Data
    public static class MonitorConfig {
        private boolean enabled;
        private String metricsPrefix;
    }

}
