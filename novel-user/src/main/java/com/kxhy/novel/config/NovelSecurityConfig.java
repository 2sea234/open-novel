package com.kxhy.novel.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class NovelSecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 非常宽松的权限配置 - 大部分接口都不需要登录
        registry.addInterceptor(new SaInterceptor(handler -> {
            SaRouter
                    // 只有少数需要用户信息的接口才检查登录
                    .match("/user/profile/**")  // 个人中心需要登录
                    .match("/novel/bookshelf/**") // 书架需要登录
                    .match("/comment") // 发表评论需要登录
                    .match("/comment/*/like") // 点赞需要登录
                    .match("/comment/*") // 删除评论需要登录（DELETE方法）
                    .notMatch("/comment/novel/*") // 获取评论不需要登录
                    .notMatch("/novel/**") // 阅读小说不需要登录
                    .notMatch("/user/register") // 注册接口
                    .notMatch("/user/login") // 登录接口
                    .notMatch("/swagger**/**") // Swagger文档
                    .notMatch("/v3/api-docs/**") // OpenAPI文档
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}