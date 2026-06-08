package com.kxhy.novel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kxhy.novel.service.TokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * token服务实现类
 */
@Service
@Log4j2
public class TokenServiceImpl implements TokenService {

    /**
     * 生成token
     * @param id 用户名
     * @return  token
     */
    @Override
    public String generateToken(Long id) {
        try {
            StpUtil.login(id);
            String token = StpUtil.getTokenValue();
            log.info("token生成成功，用户ID：{}", id);
            return token;
        } catch (Exception e) {
            log.error("token生成失败", e);
            throw new RuntimeException("token生成失败", e);
        }
    }

    /**
     * 登出
     */
    @Override
    public void logout() {
        try {

            if (StpUtil.isLogin()) {
                Object loginId = StpUtil.getLoginId();
                String userId = (String) loginId;
                StpUtil.logout();
                log.info("用户退出登陆 - 用户ID：{}", userId);
            }
        } catch (Exception e) {
            log.error("退出登陆失败", e);
            throw new RuntimeException("退出登陆失败", e);
        }
    }

    /**
     * 判断是否登录
     * @return true:已登录 false:未登录
     */
    @Override
    public Boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 获取登录用户ID
     * @return 登录用户ID
     */
    @Override
    public Object getLoginId() {
        if (!StpUtil.isLogin()) {
            throw new RuntimeException("用户未登录");
        }
        return StpUtil.getLoginId();
    }

    /**
     * 获取token
     * @return token
     */
    @Override
    public String getTokenValue() {
        return StpUtil.getTokenValue();
    }
}
