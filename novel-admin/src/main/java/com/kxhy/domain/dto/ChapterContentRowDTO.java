package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 章节内容行对象
 */
@Data
public class ChapterContentRowDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer paragraphId;
    private Integer paragraphIndex;
    private String content;

}
