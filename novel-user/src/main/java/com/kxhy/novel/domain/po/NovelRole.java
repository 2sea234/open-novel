package com.kxhy.novel.domain.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class NovelRole implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String code;
    private String description;
    private String isSystem;
    private Integer status;
    private String createTime;
    private String updateTime;
}
