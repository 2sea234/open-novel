package com.kxhy.admin.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminOperationLogQueryDTO implements Serializable {

    private final static long serialVersionUID = 1L;
    private Long adminId;

    private String username;

    private String operationModule;

    private String operationType;

    private Integer operationStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

}
