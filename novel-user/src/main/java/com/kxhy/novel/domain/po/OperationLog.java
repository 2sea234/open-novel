package com.kxhy.novel.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志记录实体类
 */
@Data
public class OperationLog implements Serializable {
    private Long id;
    private String module;        // 操作模块
    private String type;          // 操作类型
    private String description;   // 操作描述
    private String method;        // 方法名
    private String requestMethod; // 请求方式
    private String requestUrl;    // 请求URL
    private String ip;           // 操作IP
    private String location;     // 操作地点
    private String params;       // 请求参数
    private String result;       // 返回结果
    private Long userId;         // 用户ID
    private String username;     // 用户名
    private Long costTime;       // 耗时(毫秒)
    private Integer status;      // 操作状态 0-失败 1-成功
    private String errorMsg;     // 错误信息
    private LocalDateTime createTime; // 操作时间
}