package com.kxhy.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 章节查询列表
 */
@Data@AllArgsConstructor@NoArgsConstructor
public class ChapterListDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer chapterId;
    private Integer articleId; // bookId
    private Integer chapterIndex;  // chapterIndex
    private String chapterTitle; // chapterTitle
    private Integer wordCount; // wordCount
    private Integer status; // 0: draft, 1: published
    private String sourceUrl;
    private Integer displayOrder;
    private Integer isParagraphsComplete; // 0: not complete, 1: complete
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
