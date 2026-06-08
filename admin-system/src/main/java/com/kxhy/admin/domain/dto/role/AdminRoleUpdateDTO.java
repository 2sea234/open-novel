package com.kxhy.admin.domain.dto.role;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRoleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String roleName;
    private Integer status;
    private Integer sort;
    private String remark;

}
