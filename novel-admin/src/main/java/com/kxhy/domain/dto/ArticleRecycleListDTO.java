package com.kxhy.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ArticleRecycleListDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String title;
    private String authorName;
    private String categoryName;
    private Integer chapterCount;
    private LocalDateTime deletedAt;

}
