package com.kxhy.admin.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long adminId;
    private String username;
    private String operationModule;
    private String operationType;
    private String operationDesc;
    private String requestMethod;
    private String requestPath;
    private String requestIp;
    private String userAgent;
    /**
     * 1 成功，0 失败
     */
    private Integer operationStatus;
    private String errorMessage;
    private LocalDateTime operationTime;

}
