package com.kxhy.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data@AllArgsConstructor@NoArgsConstructor
public class AdminMenuVO implements Serializable {

    private Long id;

    private Long parentId;

    private String menuName;

    private Integer menuType;

    private String path;

    private String component;

    private String icon;

    private Integer sort;

    private Integer visible;

    private List<AdminMenuVO> children = new ArrayList<>();

}
