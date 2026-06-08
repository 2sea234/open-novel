package com.kxhy.novel.common.util;

import com.kxhy.novel.common.converter.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseBuilder {


    /**
     * 构建成功响应
     * @param status 状态码
     * @param message 错误信息
     * @param data 数据
     * @return 响应
     * @param <T> 响应数据类型
     */
    public <T> ResponseEntity<ApiResult<T>> buildSuccessResponse(HttpStatus status, String message, T data) {
        ApiResult<T> result = ApiResult.success(message,data);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 构建错误响应
     * @param status 状态码
     * @param message 错误信息
     * @return 响应
     * @param <T> 响应数据类型
     */
    public <T> ResponseEntity<ApiResult<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiResult<T> result = ApiResult.error(status.value(), message);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 构建错误响应
     * @param code 错误码
     * @param message 错误信息
     * @return 响应
     * @param <T> 响应数据类型
     */
    public <T> ResponseEntity<ApiResult<T>> buildErrorResponse(int code, String message) {
        ApiResult<T> result = ApiResult.error(code, message);
//        HttpStatus status = code == 401 ? HttpStatus.UNAUTHORIZED : code == 409 ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 构建限流响应
     * @return 响应
     */
    public <T> ResponseEntity<ApiResult<T>> buildRateLimitResponse() {
        ApiResult<T> result = ApiResult.error(429, "请求过于频繁请稍后重试");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
