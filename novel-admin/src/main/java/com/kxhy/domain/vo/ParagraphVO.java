package com.kxhy.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 章节内容返回对象
 */
@Data
public class ParagraphVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer paragraphId; // 段落ID
    private Integer paragraphIndex; // 段落索引
    private String content; // 段落内容

}
