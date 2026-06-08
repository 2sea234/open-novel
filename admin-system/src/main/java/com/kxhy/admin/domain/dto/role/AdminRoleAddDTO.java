package com.kxhy.admin.domain.dto.role;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRoleAddDTO implements Serializable {

    private final static long serialVersionUID = 1L;
    private String roleCode;
    private String roleName;
    private Integer status;
    private Integer sort;
    private String remark;

}
