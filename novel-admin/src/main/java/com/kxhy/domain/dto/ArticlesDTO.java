package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticlesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String coverImage;
    private String title;
    private String authorName;
    private String categoryName;
    private String summary;
    private Integer wordCount;
    private Integer chapterCount;
    private Integer isFinished;
    private String isFinishedDesc;
    private Integer status;
    private String updateTime;
    private String coverObjectName;

}
