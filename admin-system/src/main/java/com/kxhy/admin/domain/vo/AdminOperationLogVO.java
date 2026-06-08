package com.kxhy.admin.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminOperationLogVO implements Serializable {

    private final static long serialVersionUID = 1L;
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

    private Integer operationStatus;

    private String errorMessage;

    private LocalDateTime operationTime;

}
