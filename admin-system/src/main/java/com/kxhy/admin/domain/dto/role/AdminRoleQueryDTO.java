package com.kxhy.admin.domain.dto.role;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRoleQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String roleCode;
    private String roleName;
    private Integer status;

}
