package com.kxhy.novel.service;

/**
 * 令牌服务
 */
public interface TokenService {

    /**
     * 生成token并登录
     * @param id 用户id
     * @return  token值
     */
    String generateToken(Long id);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 检查是否登录
     * @return true:已登录 false:未登录
     */
    Boolean isLogin();

    /**
     * 获取登录用户ID
     * @return 登录用户ID
     */
    Object getLoginId();

    /**
     * 获取token值
     * @return token值
     */
    String getTokenValue();

}
