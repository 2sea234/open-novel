package com.kxhy.novel.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("认证管理")
                .packagesToScan("com.kxhy.novel.controller")
                .pathsToMatch("/user/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("认证管理API")
                            .description("用户注册、登录、退出等认证相关接口")
                            .version("1.0.0"));
                })
                .build();
    }


    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi emailApi() {
        return GroupedOpenApi.builder()
                .group("邮件服务")
                .pathsToMatch("/email/**")
                .build();
    }

}
