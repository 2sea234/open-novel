package com.kxhy.domain.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Chapters implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer articleId;
    private Integer chapterIndex;
    private String chapterTitle;
    private Integer wordCount;
    private Integer displayOrder;


}
