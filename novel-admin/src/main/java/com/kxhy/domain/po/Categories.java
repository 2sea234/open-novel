package com.kxhy.domain.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer status;
    private String summary;
    private String createTime;

}
