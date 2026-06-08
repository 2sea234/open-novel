package com.kxhy.novel.common.converter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一API响应格式
 * @param <T>
 */
@Data@AllArgsConstructor@NoArgsConstructor
@Schema(description = "统一API响应格式")
public class ApiResult<T> implements Serializable {

    @Schema(description = "状态码", example = "200")
    private Integer code;  // 状态码
    @Schema(description = "消息", example = "操作成功")
    private String message;  // 消息
    @Schema(description = "数据")
    private T data;  //  数据
    @Schema(description = "时间戳", example = "1648291200000")
    private Long timestamp;  // 时间戳
    @Schema(description = "请求路径", example = "/api/user/register")
    private String path;  // 请求路径

    /**
     * 成功响应（带数据）
     * @param data  数据
     * @return ApiResult
     * @param <T> 数据类型
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data, System.currentTimeMillis(), null);
    }

    /**
     * 成功响应（带消息和数据）
     * @param message 消息
     * @param data 数据
     * @return ApiResult
     * @param <T> 数据类型
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data, System.currentTimeMillis(), null);
    }

    /**
     * 成功响应
     * @param message 消息
     * @return ApiResult
     * @param <T> 数据类型
     */
    public static <T> ApiResult<T> success(String message) {
        return new ApiResult<>(200, message, null, System.currentTimeMillis(), null);
    }

    /**
     * 错误响应
     * @param code 状态码
     * @param message 消息
     * @return ApiResult
     * @param <T> 数据类型
     */
    public static <T> ApiResult<T> error(Integer code, String message) {
        return new ApiResult<>(code, message, null, System.currentTimeMillis(), null);
    }

    /**
     * 错误响应（带数据）
     * @param code 状态码
     * @param message 消息
     * @param data  数据
     * @return ApiResult
     * @param <T> 数据类型
     */
    public static <T> ApiResult<T> error(Integer code, String message, T data) {
        return new ApiResult<>(code, message, data, System.currentTimeMillis(), null);
    }

    /**
     * 设置请求路径
     * @param path 路径
     * @return ApiResult
     */
    public ApiResult<T> path(String path) {
        this.path = path;
        return this;
    }


}
