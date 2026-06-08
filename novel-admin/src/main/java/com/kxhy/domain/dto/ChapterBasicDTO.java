package com.kxhy.domain.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 章节基础信息
 */
@Data
public class ChapterBasicDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer chapterId;
    private String chapterTitle;

}
