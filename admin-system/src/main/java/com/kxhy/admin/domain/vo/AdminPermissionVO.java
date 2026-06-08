package com.kxhy.admin.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminPermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long menuId;
    private String menuName;
    private String permissionCode;
    private String permissionName;
    private Integer permissionType;
    private String requestMethod;
    private String requestPath;
    private Integer status;
    private String remark;

}
