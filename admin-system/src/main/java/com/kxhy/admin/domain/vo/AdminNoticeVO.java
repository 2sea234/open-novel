package com.kxhy.admin.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminNoticeVO implements Serializable {

    private final static long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String content;
    private Integer noticeType;
    private Integer status;
    private Integer sort;
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
