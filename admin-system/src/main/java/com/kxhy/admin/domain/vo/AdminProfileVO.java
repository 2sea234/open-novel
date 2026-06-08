package com.kxhy.admin.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminProfileVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
