package com.kxhy.novel.domain.po;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User implements Serializable {

    // 默认序列化
    private static final long serialVersionUID = 1L;
    private Long userId; // 用户id
    private String username; // 用户名
    private String password; // 密码
    private String email; // 邮箱
    private String phone; // 手机
    private String avatar; // 头像
    private String status; // 状态
    private LocalDateTime lastLoginTime; // 最后登陆时间
    private LocalDateTime createTime; // 创建时间


}
