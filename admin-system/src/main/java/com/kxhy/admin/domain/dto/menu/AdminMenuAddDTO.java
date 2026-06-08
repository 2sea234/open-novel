package com.kxhy.admin.domain.dto.menu;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminMenuAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long parentId;
    private String menuName;
    /**
     * 1 目录
     * 2 菜单
     */
    private Integer menuType;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    /**
     * 1 显示
     * 0 隐藏
     */
    private Integer visible;
    /**
     * 1 启用
     * 0 禁用
     */
    private Integer status;
    private String remark;
}
