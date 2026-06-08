package com.kxhy.admin.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminRoleListVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String roleCode;
    private String roleName;
    private Integer status;
    private Integer sort;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
