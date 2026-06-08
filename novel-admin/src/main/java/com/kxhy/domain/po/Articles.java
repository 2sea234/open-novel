package com.kxhy.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Articles implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private String authorName;
    private Integer categoryId;
    private String coverImage;
    private String summary;
    private Integer wordCount;
    private Integer chapterCount;
    private Integer isFinished;
    private Integer views;
    private Integer likes;
    private Integer isFeatured;
    private String isFinishedDesc;
    private Integer status;
    private String createTime;
    private String updateTime;
    private Long createBy;
    private Integer isDeleted;
    private Long updateBy;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private String coverObjectName;

}
