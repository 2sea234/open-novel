package com.kxhy.novel.handler;

import com.kxhy.novel.common.converter.ApiResult;
import com.kxhy.novel.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 */

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * @param e 业务异常
     * @param request 请求
     * @return 响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<?>> handleBusinessException(BusinessException e, WebRequest request) {
        String path = getRequestPath(request);
        log.warn("业务异常 - 路径：{}， 信息：{}", path, e.getMessage());

        ApiResult<?> result = ApiResult.error(e.getCode(), e.getMessage()).path(path);
        return ResponseEntity.ok(result);
    }


    /**
     * 处理参数校验异常
     * @param e 参数异常
     * @param request 请求
     * @return 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<?>> handleValidationException(MethodArgumentNotValidException e, WebRequest request) {
        String path = getRequestPath(request);
        Map<String, String > errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("参数校验失败 - 路径：{}, 错误：{}", path, errors);

        ApiResult<?> result = ApiResult.error(400, "参数校验失败", errors).path(path);
        return ResponseEntity.ok(result);
    }


    /**
     * 处理绑定异常
     * @param e 绑定异常
     * @param request 请求信息
     * @return 响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<?>> handleBinException(BindException e, WebRequest request) {
        String path = getRequestPath(request);
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("参数绑定失败 - 路径：{}，错误：{}", path, errors);

        ApiResult<?> result = ApiResult.error(400, "参数绑定失败", errors).path(path);
        return ResponseEntity.ok(result);
    }

    /**
     * 处理所有其他异常
     * @param e 其他异常
     * @param request 请求信息
     * @return 响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<?>> handleGlobalException(Exception e, WebRequest request) {
        String path = getRequestPath(request);
        log.error("系统异常 - 路径：{}", path, e);

        ApiResult<?> result = ApiResult.error(500, "系统繁忙，请稍后重试").path(path);
        return ResponseEntity.ok(result);
    }



    /**
     * 获取请求路径
     * @param request 请求
     * @return 请求路径
     */
    private String getRequestPath(WebRequest request) {

        if (request instanceof ServletWebRequest) {
            HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
            return servletRequest.getRequestURI();
        }
        return null;
    }

}
