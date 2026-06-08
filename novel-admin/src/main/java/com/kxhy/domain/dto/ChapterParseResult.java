package com.kxhy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Txt解析结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterParseResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer chapterIndex;  // 章节序列号（索引）
    private String chapterTitle;  // 章节标题
    private String content;  // 章节内容



}
