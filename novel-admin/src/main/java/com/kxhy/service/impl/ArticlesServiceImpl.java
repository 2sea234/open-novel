package com.kxhy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.component.*;
import com.kxhy.importtxt.persistence.ChapterPersistenceService;
import com.kxhy.domain.dto.*;
import com.kxhy.domain.po.*;
import com.kxhy.importtxt.support.AiCategorySupport;
import com.kxhy.importtxt.support.TxtFileReadSupport;
import com.kxhy.mapper.ArticlesMapper;
import com.kxhy.service.ArticlesService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ArticlesServiceImpl implements ArticlesService {

    private final ArticlesMapper articlesMapper;
    private final TxtParseComponent txtParseComponent;
    private final FilenameParseComponent filenameParseComponent;
    private final ContentHeaderParseComponent contentHeaderParseComponent;
    private final FieldDecisionComponent fieldDecisionComponent;
    private final ContentExcerptComponent contentExcerptComponent;
    private final ChapterPersistenceService chapterPersistenceService;
    private final TxtFileReadSupport txtFileReadSupport;
    private final AiCategorySupport aiCategorySupport;

    /**
     * novel列表
     */
    @Override
    public PageInfo<ArticlesDTO> articlesList(int pageNum, int pageSize) {
        if (pageNum <= 0 || pageSize <= 0) {
            throw new BizException(400, "分页参数不合法");
        }

        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 紧跟的查询会被分页
        List<ArticlesDTO> articlesList = articlesMapper.getArticles();
        return new PageInfo<>(articlesList);
    }

    /**
     * 查询单个novel
     */
    @Override
    public ArticlesDTO queryArticles(Integer id) {

        // 先进行参数校验 不能为空
        if (id == null) {
            throw new BizException(400, "文章ID不能为空");
        }

        ArticlesDTO articlesDTO = articlesMapper.queryArticleById(id);
        // 判断文章是否存在
        if (articlesDTO == null) {
            throw new BizException(404, "文章不存在");
        }

        return articlesDTO;
    }

    /**
     * 条件查询
     */
    @Override
    public PageInfo<ArticlesDTO> conditionArticles(int pageNum, int pageSize, String title, String authorName, Integer categoryId, Integer isFinished, Integer status) {

        if (pageNum <= 0 || pageSize <= 0) {
            throw new BizException(400, "分页参数不合法");
        }

        PageHelper.startPage(pageNum, pageSize);
        List<ArticlesDTO> articlesDTOS = articlesMapper.queryArticles(title, authorName, categoryId, isFinished, status);
        return new PageInfo<>(articlesDTOS);
    }

    /**
     * 修改
     */
    @Override
    public void updateArticles(Integer id, Articles articles, Long adminId) {

        if (id == null) {
            throw new BizException(400, "文章ID不能为空");
        }
        if (articles == null) {
            throw new BizException(400, "请求不能为空");
        }
        if (adminId == null) {
            throw new BizException(401, "当前管理员未登录");
        }
        log.info("文章修改操作人：adminId={}", adminId);
        articles.setUpdateBy(adminId);
        Integer integer = articlesMapper.updateArticles(id, articles);
        if (integer <= 0) {
            throw new BizException(404, "文章不存在，修改失败");
        }
    }

    /**
     * 修改状态
     */
    @Override
    public void updateStatus(Integer id, Integer isFinished) {

        if (id == null) {
            throw new BizException(400, "文章ID不能为空");
        }

        if (isFinished == null) {
            throw new BizException(400, "不能为空");
        }

        // 是否完结0(封禁),1(完结),2(连载),3(停更)
        if (isFinished != 0 && isFinished != 1 && isFinished != 2 && isFinished != 3) {
            throw new BizException(400, "isFinished");
        }

        Integer integer = articlesMapper.updateIsStatus(id, isFinished);
        if (integer <= 0) {
            throw new BizException(404, "文章不存在，修改失败");
        }

    }

    /**
     * 逻辑删除
     * @param articleId 文章ID
     * @param adminId 管理员ID
     */
    @Override
    public void logicalDeleteArticleById(Integer articleId, Long adminId) {
        int rows = articlesMapper.logicalDeleteArticleById(articleId, adminId);
        if (rows <= 0) {
            throw new BizException(404, "文章不存在或已删除");
        }
        log.info("文章逻辑删除成功，articleId={}, adminId={}", articleId, adminId);
    }

    /**
     * 批量逻辑删除
     * @param articleIds 文章ID列表
     * @param adminId 管理员ID
     */
    @Override
    public void batchLogicalDeleteArticles(List<Integer> articleIds, Long adminId) {

        if (articleIds == null || articleIds.isEmpty()) {
            throw new BizException(400, "文章ID列表不能为空");
        }

        // 去重
        List<Integer> distinctIds = articleIds.stream().distinct().toList();
        int count = articlesMapper.countNotDeletedArticlesByIds(distinctIds);

        if (count != distinctIds.size()) {
            throw new BizException(404, "部分文章不存在或已删除");
        }
        int rows = articlesMapper.batchLogicalDeleteArticles(distinctIds, adminId);
        if (rows <= 0) {
            throw new BizException(404, "文章不存在或已删除");
        }
    }

    /**
     * 新增书籍
     */
    @Override
    public Integer insertArticles(Articles articles, Long adminId) {

        if (adminId == null) {
            throw new BizException(401, "当前管理员未登录");
        }

        if (articles == null) {
            throw new BizException(400, "请求不能为空");
        }

        if (articles.getTitle() == null || articles.getTitle().trim().isEmpty()) {
            throw new BizException(400, "标题不能为空");
        }

        if (articles.getCategoryId() == null) {
            throw new BizException(400, "分类不能为空");
        }

        articles.setCreateBy(adminId);
        articles.setUpdateBy(adminId);

        if (articles.getWordCount() == null) {
            articles.setWordCount(0);
        }
        if (articles.getChapterCount() == null) {
            articles.setChapterCount(0);
        }
        if (articles.getViews() == null) {
            articles.setViews(0);
        }
        if (articles.getLikes() == null) {
            articles.setLikes(0);
        }
        if (articles.getStatus() == null) {
            articles.setStatus(1);
        }

        try {
            Integer rows = articlesMapper.insertArticle(articles);
            if (rows <= 0) {
                throw new BizException(500, articles.getTitle() + "添加失败");
            }
            return articles.getId();
        } catch (DuplicateKeyException e) {
            throw new BizException(400, "已存在该书，请勿重复上传相同小说");
        }
    }


    /**
     * 导入TXT
     * @param articles articles
     * @param file .txt文件
     * @param adminId 管理员ID
     * @return novelId
     */
    @Override
    public Integer importTxt(Articles articles, MultipartFile file, Long adminId) {

        if (file == null || file.isEmpty()) {
            throw new BizException(400, "TXT文件不能为空");
        }

        String filename = file.getOriginalFilename();

        // 传入文件名开始解析文件名
        FilenameParseResult filenameParseResult = filenameParseComponent.parseFilename(filename);

        if (filename == null || !filename.toLowerCase().endsWith(".txt")) {
            throw new BizException(400, "请上传.txt文件");
        }
        try {
            // 验证并读取TXT文件
            String content = txtFileReadSupport.validateAndReadTxt(file);

            String excerpt = contentExcerptComponent.buildExcerpt(content);
            // 解析TXT文件头部
            ContentHeaderParseResult contentHeaderParseResult = contentHeaderParseComponent.parseHeader(content);

            // 决策字段
            FieldDecisionResult decide = fieldDecisionComponent.decide(filenameParseResult, contentHeaderParseResult);

            // 先判断articles.getTitle如果为空则覆盖字段
            if (articles.getTitle() == null || articles.getTitle().isBlank()) {
                articles.setTitle(decide.getTitle());
            }

            // 先判断articles.getAuthorName如果为空则覆盖字段
            if (articles.getAuthorName() == null || articles.getAuthorName().isBlank()) {
                articles.setAuthorName(decide.getAuthor());
            }

            // 解析TXT文件
            List<ChapterParseResult> chapters = txtParseComponent.parseChapters(content);
            log.info("章节解析数量={}", chapters == null ? null : chapters.size());
            // 无章节标题时兜底：整个文件作为单一章节
            if (chapters == null || chapters.isEmpty()) {
                String trimmed = content.trim();
                if (trimmed.isEmpty()) {
                    throw new BizException(400, "文件内容为空");
                }
                String fallbackTitle = (articles.getTitle() != null && !articles.getTitle().trim().isEmpty())
                        ? articles.getTitle().trim() : "正文";
                ChapterParseResult fallbackChapter = new ChapterParseResult(1, fallbackTitle, trimmed);
                chapters = java.util.List.of(fallbackChapter);
            }

            try {
                AiCategoryResult result = aiCategorySupport.resolveCategory(articles.getTitle(), excerpt);
                articles.setCategoryId(result.getCategoryId());
            } catch (Exception e) {
                log.warn("AI 分类补全失败，title={}, fileName={}", articles.getTitle(), filename, e);
            }
            articles.setIsFinished(inferFinishedStatus(chapters));
            // 插入书的基本信息
            Integer articleId = insertArticles(articles, adminId);
            int totalChapterCount = chapters.size();  // 总章节数
            int totalWordCount = 0;  // 总字数

            // 逐个处理章节
            for (ChapterParseResult item :
                    chapters) {
                totalWordCount += chapterPersistenceService.saveChapter(articleId, item);
            }

            // 更新书统计信息
            articlesMapper.updateArticleStats(articleId, totalWordCount, totalChapterCount);
            return articleId;
        }
        catch (BizException e) {
            log.warn("AI 分类补全失败，title={}, fileName={}", articles.getTitle(), filename, e);
            throw e;
        }
        catch (Exception e) {
            String title = articles == null ? null : articles.getTitle();
            log.error("TXT导入失败，title={}, fileName={}", title, filename, e);
            throw new BizException(500, "文件导入失败");
        }
    }

    /**
     * 恢复文章
     * @param id 文章ID
     * @param adminId 管理员ID
     */
    @Override
    public void recoverArticleById(Integer id, Long adminId) {
        if (id == null) {
            throw new BizException(400, "文章ID不能为空");
        }
        if (adminId == null) {
            throw new BizException(401, "管理员未登录或身份无效");
        }

        Articles article = articlesMapper.selectArticleByIdIncludeDeleted(id);
        if (article == null) {
            throw new BizException(404, "文章不存在");
        }

        if (!Objects.equals(article.getIsDeleted(),1)) {
            log.info("文章未删除，无法恢复 articleId={}, adminId={}", article.getIsDeleted(), adminId);
            throw new BizException(400, "文章未删除，无法恢复");
        }

        int rows = articlesMapper.recoverArticleById(id, adminId);
        if (rows <= 0) {
            throw new BizException(500, "恢复文章失败");
        }
    }

    /**
     * 批量恢复文章
     * @param articleIds 文章ID列表
     * @param adminId 管理员ID
     */
    @Override
    public void batchRecoverArticles(List<Integer> articleIds, Long adminId) {
        if (articleIds == null || articleIds.isEmpty()) {
            throw new BizException(400, "文章ID列表不能为空");
        }
        if (adminId == null) {
            throw new BizException(401, "管理员未登录或身份无效");
        }

        // 1. 先去重、去掉 null
        List<Integer> distinctIds = articleIds.stream()
                .filter(Objects::nonNull)  // 把 null 去掉
                .distinct() // 去重
                .collect(Collectors.toList());

        if (distinctIds.isEmpty()) {
            throw new BizException(400, "文章ID列表不能为空");
        }

        // 2. 查询对应  id 的删除状态
        List<Articles> articleList = articlesMapper.selectArticleDeleteStatusByIds(distinctIds);
        // 3. 把数据库查到的 id 提出来，组成一个集合
        Set<Integer> dbIds = articleList.stream()
                .map(Articles::getId)
                .collect(Collectors.toSet());

        // 5. 传入 dbIds - 数据库查到的 dbIds = 不存在的 dbIds [算出“不存在的 id”]
        List<Integer> notFoundIds = distinctIds.stream()
                .filter(id -> !dbIds.contains(id))
                .collect(Collectors.toList());

        // 6. 未删除的 id = 数据库存在，但 is_deleted != 1  [算出“存在但未删除，不能恢复的 id”]
        List<Integer> notDeletedIds = articleList.stream()
                .filter(item -> !Objects.equals(item.getIsDeleted(), 1))
                .map(Articles::getId)
                .sorted()
                .collect(Collectors.toList());

        // 7. 只要 id 有问题，就拼接错误信息并报错
        if (!notFoundIds.isEmpty() || !notDeletedIds.isEmpty()) {
            StringBuilder msg = new StringBuilder();

            if (!notFoundIds.isEmpty()) {
                msg.append("以下文章不存在: ").append(notFoundIds);
            }

            if (!notDeletedIds.isEmpty()) {
                if (msg.length() > 0) {
                    msg.append("；");
                }
                for (Articles item : articleList) {
                    log.info("id={}, isDeleted={}", item.getId(), item.getIsDeleted());
                }
                msg.append("无法恢复: ").append(notDeletedIds).append(". 原因：文章未删除");
            }
            throw new BizException(400, msg.toString());
        }

        int rows = articlesMapper.batchRecoverArticles(distinctIds, adminId);
        if (rows != distinctIds.size()) {
            throw new BizException(500, "批量恢复失败，请稍后重试");
        }
    }

    /**
     * 查询回收站中的文章列表
     */
    @Override
    public PageInfo<ArticleRecycleListDTO> queryDeletedArticles(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ArticleRecycleListDTO> list = articlesMapper.queryRecycleArticles();
        return new PageInfo<>(list);
    }

    /**
     * 解析TXT文件返回预览信息
     * @param file 文件
     * @return 预览信息
     */
    @Override
    public TxtImportPreviewResponse parseTxtPreview(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BizException(400, "文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new BizException(400, "文件名不能为空");
        }
        // 读取TXT文件
        String content = txtFileReadSupport.validateAndReadTxt(file);
        String excerpt = contentExcerptComponent.buildExcerpt(content);
        // 解析文件名获取小说名
        FilenameParseResult filenameParseResult = filenameParseComponent.parseFilename(filename);
        // 解析文件头(主要是查看是否有作者和书籍标题)
        ContentHeaderParseResult contentHeaderParseResult = contentHeaderParseComponent.parseHeader(content);
        // 决策决定最终的书籍和标题
        FieldDecisionResult decide = fieldDecisionComponent.decide(filenameParseResult, contentHeaderParseResult);
        TxtImportPreviewResponse preview = new TxtImportPreviewResponse();
        preview.setTitle(decide.getTitle());
        preview.setAuthorName(decide.getAuthor());
        try {

            AiCategoryResult result = aiCategorySupport.resolveCategory(preview.getTitle(), excerpt);

            // 再次判断：如果categories不为空，则进行赋值
            if (result != null) {
                // 将不为空的categories 赋给preview
                preview.setCategoryId(result.getCategoryId());
                preview.setCategoryName(result.getCategoryName());
                preview.setSummary(result.getSummary());
            }


        }catch (Exception e) {
            log.warn("TXT预解析AI分类失败，fileName={}", filename, e);
        }

        return preview;
    }


    private Integer inferFinishedStatus(List<ChapterParseResult> chapters) {
        if (chapters == null || chapters.isEmpty()) {
            return 2; // 连载中
        }

        int start = Math.max(0, chapters.size() - 5);

        for (int i = start; i < chapters.size(); i++) {
            ChapterParseResult chapter = chapters.get(i);
            if (chapter == null || chapter.getChapterTitle() == null) {
                continue;
            }

            String title = chapter.getChapterTitle();

            if (title.contains("大结局")
                    || title.contains("完结")
                    || title.contains("番外")
                    || title.contains("终章")) {
                return 1; // 完结
            }
        }

        return 2; // 连载中
    }

}
