package com.kxhy.novel.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor@NoArgsConstructor
@Schema(description = "登录/注册返回结果")
public class UserRegisterResult implements Serializable {

    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "访问令牌")
    private String token; // 注册后自动登录返回的token
    @Schema(description = "操作时间")
    private String operationTime;
    @Schema(description = "用户状态")
    private String status; // 用户状态

}
