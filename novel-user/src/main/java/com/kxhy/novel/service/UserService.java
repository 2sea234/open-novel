package com.kxhy.novel.service;

import com.kxhy.novel.domain.dto.UserDTO;
import com.kxhy.novel.domain.dto.UserRegisterDTO;
import com.kxhy.novel.domain.vo.UserRegisterResult;
import com.kxhy.novel.domain.vo.UsernameCheckResult;


public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username 用户名
     * @return true:存在 false:不存在
     */
    Boolean isUsernameExists(String username);

    /**
     * 判断邮箱是否存在
     * @param email 邮箱
     * @return true:存在 false:不存在
     */
    Boolean isUserEmailExists(String email);

    /**
     * 用户注册
     * @param userDTO 用户信息
     * @return 注册结果
     */
    UserRegisterResult register(UserRegisterDTO userDTO);

    /**
     * 用户登录
     * @param userDTO 用户信息
     * @return 用户实体
     */
    UserRegisterResult login(UserRegisterDTO userDTO);

    /**
     * 退出登录
     * @return true:退出成功 false:退出失败
     */
    Boolean logout();

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    Object getCurrentUserInfo();

    /**
     * 检查用户名是否可用  <span style="color: red" >未启用  有待考虑<span/>
     * @param username 用户名
     * @return 检查结果
     */
    UsernameCheckResult checkUsernameAvailability(String username);


    UserDTO updateUserInfo(UserDTO userDTO);

}