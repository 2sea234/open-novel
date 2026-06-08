package com.kxhy.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ParagraphAnchors implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long anchorId;
    private Integer articleId;
    private Integer chapterIndex;
    private String chapterTitle;
    private Integer paragraphIndex;
    private String anchorHash;
    private Integer occurrenceNo;
    private Long paragraphId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
