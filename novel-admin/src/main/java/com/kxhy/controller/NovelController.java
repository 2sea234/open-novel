package com.kxhy.controller;


import com.github.pagehelper.PageInfo;
import com.kxhy.domain.dto.ArticleRecycleListDTO;
import com.kxhy.domain.dto.ArticlesDTO;
import com.kxhy.domain.dto.ChapterListDTO;
import com.kxhy.domain.dto.TxtImportPreviewResponse;
import com.kxhy.domain.po.Articles;
import com.kxhy.domain.vo.ChapterContentVO;
import com.kxhy.service.ArticlesService;
import com.kxhy.service.ChapterService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.jwt.JwtProperties;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class NovelController {


    private final ArticlesService articlesService;
    private final ChapterService chapterService;

    /**
     * 分页查询文章列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return novel列表
     */
    @GetMapping("articlesList/{pageNum}/{pageSize}")
    public Result<PageInfo<ArticlesDTO>> articlesList(@RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                                      @RequestHeader(value = "X-Admin-Username", required = false) String username,
                                                      @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        log.info("当前管理员 adminId={}, username={}", adminId, username);

        return Result.success(articlesService.articlesList(pageNum, pageSize));
    }

    /**
     * 根据文章ID查询详情
     */
    @GetMapping("queryArticles/{id}")
    public Result<ArticlesDTO> queryArticles(@PathVariable Integer id) {
        return Result.success(articlesService.queryArticles(id));
    }

    /**
     * 调价分页查询
     */
    @GetMapping("conditionArticles")
    public Result<PageInfo<ArticlesDTO>> conditionArticles(@RequestParam Integer pageNum,
                                                           @RequestParam Integer pageSize,
                                                           @RequestParam(required = false) String title,
                                                           @RequestParam(required = false) String authorName,
                                                           @RequestParam(required = false) Integer categoryId,
                                                           @RequestParam(required = false) Integer isFinished,
                                                           @RequestParam(required = false) Integer status) {
        return Result.success(articlesService.conditionArticles(pageNum, pageSize, title, authorName, categoryId, isFinished, status));
    }

    /**
     * 修改novel基本信息
     */
    @RequirePermission("admin:novel:update")
    @PutMapping("updateArticles/{id}")
    public Result<Void> updateArticles(@PathVariable Integer id, @RequestBody Articles articles,
                                       @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                       @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        log.info("修改novel基本信息，当前管理员 adminId={}, username={}, articleId={}", adminId, username, id);
        articlesService.updateArticles(id, articles, adminId);
        return Result.success();
    }

    /**
     * 修改novel状态
     */
    @RequirePermission("admin:novel:status")
    @RequestMapping(value = "updateIsStatus/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
    public Result<Void> updateIsStatus(@PathVariable Integer id, @RequestParam Integer status) {
        articlesService.updateStatus(id, status);
        return Result.success();
    }


    /**
     * 单本删除
     */
    @RequirePermission("admin:novel:delete")
    @DeleteMapping("onDel/{articleId}")
    public Result<Void> deleteArticle(@PathVariable Integer articleId, @RequestHeader(value = "X-Admin-Id") Long adminId) {
        articlesService.logicalDeleteArticleById(articleId, adminId);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @RequirePermission("admin:novel:delete")
    @DeleteMapping("manyDel/batch")
    public Result<Void> batchDeleteArticles(@RequestBody List<Integer> articleIds, @RequestHeader(value = "X-Admin-Id") Long adminId) {
        articlesService.batchLogicalDeleteArticles(articleIds, adminId);
        return Result.success();
    }


    /**
     * 添加novel
     */
    @RequirePermission("admin:novel:add")
    @PostMapping("addNovel")
    public Result<Integer> addNovel(@RequestBody Articles articles, @RequestHeader(value = "X-Admin-Id", required = false) Long adminId) {
        String coverImage = articles.getCoverImage();
        if (coverImage != null && !coverImage.isBlank()) {
            String coverObjectName = extractCoverObjectName(coverImage);
            articles.setCoverObjectName(coverObjectName);
        }
        Integer integer = articlesService.insertArticles(articles, adminId);
        return Result.success(integer);
    }

    private String extractCoverObjectName(String coverImage) {
        String normalized = coverImage.trim().replace("\\","/");

        int coverIndex = normalized.indexOf("cover/");
        if (coverIndex >= 0) {
            return normalized.substring(coverIndex);
        }

        int lastIndexOf = normalized.lastIndexOf("/");
        if (lastIndexOf >= 0 && lastIndexOf < normalized.length() - 1) {
            return normalized.substring(lastIndexOf + 1);
        }

        return normalized;
    }


    /**
     * 导入txt
     */
    @RequirePermission("admin:novel:import")
    @PostMapping("importTxt")
    public Result<Integer> importTxt(@ModelAttribute Articles articles,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestHeader(value = "X-Admin-Id", required = false) Long adminId) {
        Integer articleId = articlesService.importTxt(articles, file, adminId);
        return Result.success(articleId);
    }


    /**
     * 单本恢复
     */
    @RequirePermission("admin:recycle:recover")
    @PutMapping("recover/{articleId}")
    public Result<Void> recoverArticleById(@PathVariable Integer articleId, @RequestHeader("X-Admin-Id") Long adminId) {
        articlesService.recoverArticleById(articleId, adminId);
        return Result.success();
    }

    /**
     * 批量恢复
     */
    @RequirePermission("admin:novel:recover")
    @PutMapping("batchRecover")
    public Result<Void> batchRecoverArticles(@RequestBody List<Integer> articleIds, @RequestHeader("X-Admin-Id") Long adminId) {
        articlesService.batchRecoverArticles(articleIds, adminId);
        return Result.success();
    }

    /**
     * 回收站列表
     */
    @GetMapping("/recycle/articles/{pageNum}/{pageSize}")
    public Result<PageInfo<ArticleRecycleListDTO>> queryDeletedArticles(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        PageInfo<ArticleRecycleListDTO> pageInfo = articlesService.queryDeletedArticles(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 章节列表
     */
    @GetMapping("/chapters/{articleId}/{pageNum}/{pageSize}")
    public Result<PageInfo<ChapterListDTO>> chapterList(@PathVariable Integer articleId, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        PageInfo<ChapterListDTO> chapterDTOPageInfo = chapterService.queryChapterList(articleId, pageNum, pageSize);




        return Result.success(chapterDTOPageInfo);
    }

    /**
     * 章节内容
     */
    @GetMapping("/chapter/{chapterId}")
    public Result<ChapterContentVO> chapterContext(@PathVariable Integer chapterId) {
        ChapterContentVO chapterContentVO = chapterService.queryChapterContext(chapterId);
        return Result.success(chapterContentVO);
    }

    /**
     * 预解析，返回给前端自动回填的数据
     * @param file 文件
     * @return 预解析数据
     */
    @PostMapping("/parse-txt")
    public Result<TxtImportPreviewResponse> parseTxt(@RequestParam("file") MultipartFile file) {
        TxtImportPreviewResponse txtImportPreviewResponse = articlesService.parseTxtPreview(file);
        return Result.success(txtImportPreviewResponse);
    }

}
