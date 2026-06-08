package com.kxhy.admin.domain.dto.notice;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class AdminNoticeUpdateDTO implements Serializable {

    private final static long serialVersionUID = 1L;
    private String title;
    private String content;
    private Integer noticeType;
    private Integer status;
    private Integer sort;
}
