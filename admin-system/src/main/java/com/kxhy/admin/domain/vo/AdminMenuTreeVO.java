package com.kxhy.admin.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AdminMenuTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<AdminMenuTreeVO> children = new ArrayList<>();

}
