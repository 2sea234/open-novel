package com.kxhy.novel.pool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kxhy.novel.config.EmailConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class EmailConnectionPool {

    private GenericObjectPool<SimpleEmail> pool;

    @Autowired
    private EmailConfig emailConfig;

    @PostConstruct
    public void init() {
        try {
            GenericObjectPoolConfig<SimpleEmail> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(emailConfig.getPool().getMaxTotal());
            config.setMaxIdle(emailConfig.getPool().getMaxIdle());
            config.setMinIdle(emailConfig.getPool().getMinIdle());
            config.setMaxWaitMillis(emailConfig.getPool().getMaxWaitMillis());
            config.setTestOnBorrow(emailConfig.getPool().isTestOnBorrow());
            config.setTestWhileIdle(emailConfig.getPool().isTestWhileIdle());

            // 连接池空闲检查配置
            config.setTimeBetweenEvictionRunsMillis(30000);
            config.setMinEvictableIdleTimeMillis(60000);
            config.setNumTestsPerEvictionRun(3);

            pool = new GenericObjectPool<>(new EmailObjectFactory(emailConfig), config);

            // 预热连接池
            prewarmPool(emailConfig.getPool().getMinIdle());

            log.info("邮件连接池初始化完成，配置: {}", emailConfig.getPool());

        } catch (Exception e) {
            log.error("邮件连接池初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("邮件连接池初始化失败", e);
        }
    }

    /**
     * 预热连接池
     */
    private void prewarmPool(int count) {
        for (int i = 0; i < count; i++) {
            try {
                pool.addObject();
            } catch (Exception e) {
                log.warn("预热连接池对象失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 借出对象 - 用于测试连接
     */
    public SimpleEmail borrowObject() throws Exception {
        return pool.borrowObject();
    }

    /**
     * 归还对象
     */
    public void returnObject(SimpleEmail email) {
        try {
            pool.returnObject(email);
        } catch (Exception e) {
            log.warn("归还邮件对象失败: {}", e.getMessage());
            try {
                pool.invalidateObject(email);
            } catch (Exception ex) {
                // 忽略
            }
        }
    }

    /**
     * 使对象失效
     */
    public void invalidateObject(SimpleEmail email) {
        try {
            pool.invalidateObject(email);
        } catch (Exception e) {
            log.warn("使邮件对象失效失败: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void close() {
        if (pool != null && !pool.isClosed()) {
            pool.close();
            log.info("邮件连接池已关闭");
        }
    }

    /**
     * 获取连接池统计信息
     */
    public String getPoolStats() {
        if (pool == null || pool.isClosed()) {
            return "Pool not initialized or closed";
        }
        return String.format("Active: %d, Idle: %d, Waiting: %d, Total: %d",
                pool.getNumActive(),
                pool.getNumIdle(),
                pool.getNumWaiters(),
                pool.getNumIdle() + pool.getNumActive());
    }

    /**
     * 获取活跃连接数
     */
    public int getNumActive() {
        return pool != null ? pool.getNumActive() : 0;
    }

    /**
     * 获取空闲连接数
     */
    public int getNumIdle() {
        return pool != null ? pool.getNumIdle() : 0;
    }

    /**
     * 内部对象工厂
     */
    private static class EmailObjectFactory extends BasePooledObjectFactory<SimpleEmail> {
        private final EmailConfig emailConfig;

        public EmailObjectFactory(EmailConfig emailConfig) {
            this.emailConfig = emailConfig;
        }

        @Override
        public SimpleEmail create() throws Exception {
            SimpleEmail email = new SimpleEmail();
            EmailConfig.SmtpConfig smtp = emailConfig.getSmtp();

            email.setHostName(smtp.getHost());
            email.setSmtpPort(smtp.getPort());
            email.setSSLOnConnect(smtp.isSslEnabled());
            email.setStartTLSEnabled(true);
            email.setFrom(smtp.getFromEmail());
            email.setAuthentication(smtp.getFromEmail(), smtp.getAuthCode());
            email.setCharset("UTF-8");
            email.setSocketConnectionTimeout(smtp.getConnectionTimeout());
            email.setSocketTimeout(smtp.getTimeout());

            return email;
        }

        @Override
        public PooledObject<SimpleEmail> wrap(SimpleEmail email) {
            return new DefaultPooledObject<>(email);
        }

        @Override
        public boolean validateObject(PooledObject<SimpleEmail> pooledObject) {
            try {
                SimpleEmail email = pooledObject.getObject();
                // 验证对象是否可用：检查必要的配置
                return email.getHostName() != null &&
                        email.getFromAddress() != null &&
                        email.getMimeMessage() == null; // 确保没有构建过 MimeMessage
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void destroyObject(PooledObject<SimpleEmail> pooledObject) throws Exception {
            // 清理资源
            SimpleEmail email = pooledObject.getObject();
            // 不需要显式清理，让 GC 处理
        }

        @Override
        public void activateObject(PooledObject<SimpleEmail> pooledObject) throws Exception {
            // 激活对象时确保状态正确
            SimpleEmail email = pooledObject.getObject();
            // 确保没有旧的 MimeMessage
            // 注意：SimpleEmail 不提供重置方法，所以我们依赖 validateObject 来过滤
        }
    }
}