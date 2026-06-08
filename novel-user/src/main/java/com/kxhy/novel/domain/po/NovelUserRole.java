package com.kxhy.novel.domain.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class NovelUserRole implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long userId;
    private Long roleId;
    private String createTime;
    private String updateTime;

}
