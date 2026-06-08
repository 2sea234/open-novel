package com.kxhy.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 章节内容
 */
@Data
public class ChapterContentVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer chapterId;
    private String chapterTitle;
    private List<ParagraphVO> paragraphs;

}
