package com.kxhy.novel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "获取当前用户信息")
public class CurrentUserVo implements Serializable {

    @Schema(description = "序列化")
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名成")
    private String username;
    /*
    @Schema(description = "手机号")
    private String phone;
    */
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1"})
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
