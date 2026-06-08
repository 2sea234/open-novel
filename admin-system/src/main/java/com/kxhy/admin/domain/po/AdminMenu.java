package com.kxhy.admin.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminMenu implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer parentId;
    private String menuName;
    private Integer menuType;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private String remark;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private Integer createBy;
    private Integer updateBy;
    private Integer is_deleted;

}
