package com.kxhy.admin.domain.dto.menu;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminMenuUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long parentId;
    private String menuName;
    private Integer menuType;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private String remark;

}
