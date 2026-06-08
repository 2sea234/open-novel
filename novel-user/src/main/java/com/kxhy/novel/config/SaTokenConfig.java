package com.kxhy.novel.config;

import cn.dev33.satoken.SaManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaTokenConfig {

    @PostConstruct
    public void init() {
        // 创建配置对象
        cn.dev33.satoken.config.SaTokenConfig config = new cn.dev33.satoken.config.SaTokenConfig();

        // 设置配置参数
        config.setTokenName("novel-token");              // token名称
        config.setTimeout(7 * 24 * 60 * 60);             // token有效期，单位秒（7天）
        config.setActiveTimeout(2 * 60 * 60);            // 临时有效期（2小时无操作过期）
        config.setIsConcurrent(true);                    // 是否允许同一账号并发登录
        config.setIsShare(true);                         // 在多人登录同一账号时，是否共享token
        config.setMaxLoginCount(3);                      // 同一账号最大登录数量

        // Token风格（可选）
        config.setTokenStyle("uuid");                    // token风格

        // 是否输出操作日志（开发环境建议开启）
        config.setIsLog(true);

        // 是否尝试从header里读取token
        config.setIsReadHeader(true);

        // 是否尝试从cookie里读取token
        config.setIsReadCookie(false);

        // 是否尝试从请求体里读取token
        config.setIsReadBody(false);

        // 关闭自动续签（如果需要可以开启）
        config.setAutoRenew(false);

        // 将配置注入到SaToken管理器中
        SaManager.setConfig(config);

        System.out.println("SaToken配置初始化完成");
    }
}