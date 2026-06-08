package com.kxhy.novel.exception;

import lombok.Data;

/**
 * 业务异常
 */
@Data
public class BusinessException extends RuntimeException {


    private Integer code;
    // private String message; // （可选） 错误信息

    public BusinessException(String message) {
        super(message);
        // this.message = message;
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        // this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        // this.message = message;
        this.code = 400;
    }

    public Integer getCode() {
        return code;
    }

}
