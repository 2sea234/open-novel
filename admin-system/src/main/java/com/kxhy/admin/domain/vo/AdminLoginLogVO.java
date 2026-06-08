package com.kxhy.admin.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminLoginLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private Integer loginStatus;

    private String loginIp;

    private String userAgent;

    private String errorMessage;

    private LocalDateTime loginTime;

}
