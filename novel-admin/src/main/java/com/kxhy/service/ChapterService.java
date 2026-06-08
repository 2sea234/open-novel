package com.kxhy.service;

import com.github.pagehelper.PageInfo;
import com.kxhy.domain.dto.ChapterContentRowDTO;
import com.kxhy.domain.dto.ChapterListDTO;
import com.kxhy.domain.po.Chapters;
import com.kxhy.domain.vo.ChapterContentVO;

import java.util.List;

public interface ChapterService {

    /**
     * 查询章节列表
     *
     * @param articleId 书籍ID
     * @return 章节列表
     */
    PageInfo<ChapterListDTO> queryChapterList(Integer articleId, int pageNum, int pageSize);

    /**
     * 查询章节内容
     * @param chapterId 章节ID
     * @return 章节内容
     */
    ChapterContentVO queryChapterContext(Integer chapterId);

}
