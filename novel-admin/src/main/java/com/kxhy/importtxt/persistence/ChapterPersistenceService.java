package com.kxhy.importtxt.persistence;


import com.kxhy.component.TxtParseComponent;
import com.kxhy.domain.dto.AnchorDraft;
import com.kxhy.domain.dto.ChapterParseResult;
import com.kxhy.domain.dto.ParagraphBuildResult;
import com.kxhy.domain.po.Chapters;
import com.kxhy.domain.po.ParagraphAnchors;
import com.kxhy.domain.po.Paragraphs;
import com.kxhy.mapper.ChaptersMapper;
import com.kxhy.mapper.ParagraphAnchorsMapper;
import com.kxhy.mapper.ParagraphsMapper;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单章落库 + 段落组装 + 锚点组装 + 批量保存
 */
@Service
@RequiredArgsConstructor
public class ChapterPersistenceService {

    private final ChaptersMapper chaptersMapper;
    private final ParagraphsMapper paragraphsMapper;
    private final ParagraphAnchorsMapper paragraphAnchorsMapper;
    private final TxtParseComponent txtParseComponent;


    /**
     * 单章落库 + 单章段落/锚点处理
     */
    public int saveChapter(Integer articleId, ChapterParseResult item) {
        List<String> paragraphList = txtParseComponent.parseParagraphs(item.getContent());

        int chapterWordCount = txtParseComponent.countParagraphListWordCount(paragraphList);
        Chapters chapter = new Chapters();
        chapter.setArticleId(articleId);
        chapter.setChapterIndex(item.getChapterIndex());
        chapter.setChapterTitle(item.getChapterTitle());
        chapter.setDisplayOrder(item.getChapterIndex());
        chapter.setWordCount(chapterWordCount);
        // 先插入章节拿到chapterId
        chaptersMapper.insertChapter(chapter);

        if (chapter.getId() == null) {
            throw new BizException(500, "插入章节失败，未获取到章节ID");
        }
        Long chapterId = chapter.getId().longValue();
        ParagraphBuildResult paragraphBuildResult = buildParagraphs(chapterId,
                item.getChapterIndex(),
                item.getChapterTitle(),
                paragraphList);
        List<Paragraphs> batchList = paragraphBuildResult.getBatchList();
        List<AnchorDraft> anchorDraftList = paragraphBuildResult.getAnchorDraftList();
        // 批量保存段落
        saveParagraphsBatch(batchList);
        List<ParagraphAnchors> anchorsList = buildAnchorsList(batchList, anchorDraftList, articleId);
        if (!anchorsList.isEmpty()) {
            paragraphAnchorsMapper.insertBatchParagraphAnchors(anchorsList);
        }

        return chapterWordCount;
    }

    /**
     * 组装
     */
    private ParagraphBuildResult buildParagraphs(Long chapterId, Integer chapterIndex,
                                                 String chapterTitle, List<String> paragraphList) {
        List<Paragraphs> batchList = new ArrayList<>(paragraphList.size());
        List<AnchorDraft> anchorDraftList = new ArrayList<>();

        Map<String, Integer> occurrenceMap = new HashMap<>();
        for (int i = 0; i < paragraphList.size(); i++) {
            String paragraphContent = paragraphList.get(i);

            String normalizedContent = txtParseComponent.normalizeParagraphContent(paragraphContent);
            String anchorHash = txtParseComponent.generateAnchorHash(chapterIndex, normalizedContent);

            int occurrenceNo = occurrenceMap.getOrDefault(anchorHash, 0) + 1;
            occurrenceMap.put(anchorHash, occurrenceNo);

            // 段落
            Paragraphs paragraphs = new Paragraphs();
            paragraphs.setChapterId(chapterId);
            paragraphs.setParagraphIndex(i);
            paragraphs.setContent(paragraphContent);
            paragraphs.setParagraphHash(txtParseComponent.generateParagraphHash(chapterId, i, paragraphContent));
            batchList.add(paragraphs);

            // 锚点
            AnchorDraft draft = new AnchorDraft();
            draft.setChapterIndex(chapterIndex);
            draft.setChapterTitle(chapterTitle);
            draft.setParagraphIndex(i);
            draft.setAnchorHash(anchorHash);
            draft.setOccurrenceNo(occurrenceNo);
            anchorDraftList.add(draft);
        }
        ParagraphBuildResult result = new ParagraphBuildResult();
        result.setBatchList(batchList);
        result.setAnchorDraftList(anchorDraftList);
        return result;
    }

    /**
     * 二轮合并
     */
    private List<ParagraphAnchors> buildAnchorsList(List<Paragraphs> batchList, List<AnchorDraft> anchorDraftList, Integer articleId) {

        List<ParagraphAnchors> anchorsList = new ArrayList<>(batchList.size());
        for (int i = 0; i < batchList.size(); i++) {
            Paragraphs paragraph = batchList.get(i);  // 拿到数据库回写的ID
            AnchorDraft draft = anchorDraftList.get(i);

            ParagraphAnchors anchors = new ParagraphAnchors();
            anchors.setParagraphId(paragraph.getId());
            anchors.setArticleId(articleId);
            anchors.setChapterIndex(draft.getChapterIndex());
            anchors.setChapterTitle(draft.getChapterTitle());
            anchors.setParagraphIndex(draft.getParagraphIndex());
            anchors.setAnchorHash(draft.getAnchorHash());
            anchors.setOccurrenceNo(draft.getOccurrenceNo());
            anchorsList.add(anchors);
        }
        return anchorsList;
    }

    /**
     * 批量保存
     */
    private void saveParagraphsBatch(List<Paragraphs> batchList) {
        if (batchList == null || batchList.isEmpty()) {
            return;
        }

        int batchSize = 200;

        for (int i = 0; i < batchList.size(); i+= batchSize) {
            int end = Math.min(i + batchSize, batchList.size());
            List<Paragraphs> subList = batchList.subList(i, end);
            paragraphsMapper.insertBatchParagraphs(subList);
        }
    }

}
