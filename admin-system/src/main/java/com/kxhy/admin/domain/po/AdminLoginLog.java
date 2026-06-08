package com.kxhy.admin.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    /**
     * 1 成功，0 失败
     */
    private Integer loginStatus;
    private String loginIp;
    private String userAgent;
    private String errorMessage;
    private LocalDateTime loginTime;

}
