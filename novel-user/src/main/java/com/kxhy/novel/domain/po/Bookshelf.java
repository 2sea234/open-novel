package com.kxhy.novel.domain.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Bookshelf implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer userId;
    private Integer articleId;
    private Integer lastReadChapterId;
    private Integer LastReadParagraphIndex;
    private String addedTime;
    private String lastReadTime;
    private Integer status;
    private String remark;

}
