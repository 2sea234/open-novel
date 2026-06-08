package com.kxhy.admin.domain.dto.notice;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminNoticeStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer status; // 上下架状态

}
