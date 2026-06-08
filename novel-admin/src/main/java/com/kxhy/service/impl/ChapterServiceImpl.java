package com.kxhy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.domain.dto.ChapterBasicDTO;
import com.kxhy.domain.dto.ChapterContentRowDTO;
import com.kxhy.domain.dto.ChapterListDTO;
import com.kxhy.domain.vo.ChapterContentVO;
import com.kxhy.domain.vo.ParagraphVO;
import com.kxhy.mapper.ChaptersMapper;
import com.kxhy.service.ChapterService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChaptersMapper chaptersMapper;

    @Override
    public PageInfo<ChapterListDTO> queryChapterList(Integer articleId, int pageNum, int pageSize) {

        if (pageNum <= 0 || pageSize <= 0) {
            throw new BizException(400, "分页参数不合法");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<ChapterListDTO> chaptersList = chaptersMapper.selectChaptersByArticleId(articleId);
        return new PageInfo<>(chaptersList);
    }

    @Override
    public ChapterContentVO queryChapterContext(Integer chapterId) {

        if (chapterId == null || chapterId <= 0) {
            throw new BizException(400, "参数不合法");
        }

        // 先判断章节是否存在
        ChapterBasicDTO chapterBasicDTO = chaptersMapper.selectChapterById(chapterId);
        if (chapterBasicDTO == null) {
            throw new BizException(404, "章节不存在");
        }

        // 拿段落行数据
        List<ChapterContentRowDTO> rows = chaptersMapper.selectChapterContentsByChapterId(chapterId);
        if (rows == null || rows.isEmpty()) {
            throw new BizException(404, "章节内容不存在");
        }

        // 组装最终返回
        ChapterContentVO chapterContentVO = new ChapterContentVO();
        List<ParagraphVO> paragraphVOS = new ArrayList<>();
        chapterContentVO.setChapterId(chapterBasicDTO.getChapterId());
        chapterContentVO.setChapterTitle(chapterBasicDTO.getChapterTitle());

        for (ChapterContentRowDTO rowsDTO : rows) {
            // 组装最终返回
            ParagraphVO paragraphVO = new ParagraphVO();
            // 段落信息
            paragraphVO.setParagraphId(rowsDTO.getParagraphId());
            paragraphVO.setParagraphIndex(rowsDTO.getParagraphIndex());
            paragraphVO.setContent(rowsDTO.getContent());
            paragraphVOS.add(paragraphVO);
        }
        chapterContentVO.setParagraphs(paragraphVOS);
        // 返回最终结果
        return chapterContentVO;
    }
}
