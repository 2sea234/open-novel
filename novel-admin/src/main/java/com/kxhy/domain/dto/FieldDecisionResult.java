package com.kxhy.domain.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * title 字段决策器
 * @author kxhy
 * @date 2021/5/27 10:07 上午
 */
@Data
public class FieldDecisionResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String author;


}
