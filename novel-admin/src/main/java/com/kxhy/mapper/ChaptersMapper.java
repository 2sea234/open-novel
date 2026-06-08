package com.kxhy.mapper;

import com.kxhy.domain.dto.ChapterBasicDTO;
import com.kxhy.domain.dto.ChapterContentRowDTO;
import com.kxhy.domain.dto.ChapterListDTO;
import com.kxhy.domain.po.Chapters;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChaptersMapper {




    /**
     * 添加章节
     * @param chapter 章节
     * @return 添加结果
     */
    Integer insertChapter(Chapters chapter);

    /**
     * 根据章节id查询章节
     * @param chapterId 章节id
     * @return 章节
     */
    ChapterBasicDTO selectChapterById(Integer chapterId);


    /**
     * 章节列表查询
     * @param articleId 书籍ID
     * @return 章节列表
     */
    List<ChapterListDTO> selectChaptersByArticleId(@Param("articleId") Integer articleId);


    /**
     * 查询但章节内容
     * @param chapterId 章节ID
     * @return 章节内容
     */
    List<ChapterContentRowDTO> selectChapterContentsByChapterId(@Param("chapterId") Integer chapterId);

}
