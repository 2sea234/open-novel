package com.kxhy.novel.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Data@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户注册/登录请求参数")
public class UserRegisterDTO implements Serializable {

    // 默认序列化
    private static final long serialVersionUID = 1L;

    private Long userId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 10, message = "用户名长度在3-10个字符之间")
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6-20个字符之间")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Pattern(regexp = "^1[3-9]\\d{8}$", message = "手机格式不正确")
    @Schema(description = "手机号", example = "13847844565")
    private String mobile;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "example@example.com")
    private String email;

    @Schema(description = "验证码", example = "123456")
    private String code;



}
