package com.kxhy.admin.domain.dto.notice;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminNoticeAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String content;
    private Integer noticeType;
    private Integer status;
    private Integer sort;
    private Long createBy;
    private Long updateBy;

}
