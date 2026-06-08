package com.kxhy.service;

import com.github.pagehelper.PageInfo;
import com.kxhy.domain.dto.ArticleRecycleListDTO;
import com.kxhy.domain.dto.ArticlesDTO;
import com.kxhy.domain.dto.ChapterParseResult;
import com.kxhy.domain.dto.TxtImportPreviewResponse;
import com.kxhy.domain.po.Articles;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ArticlesService {

    /**
     * 查询所有novel
     * @return novel列表
     */
    PageInfo<ArticlesDTO> articlesList(int pageNum, int pageSize);

    /**
     * 根据id查询novel
     * @param id novel id
     * @return novel
     */
    ArticlesDTO queryArticles(Integer id);

    /**
     * 多条件查询
     */
    PageInfo<ArticlesDTO> conditionArticles(int pageNum, int pageSize, String title,
                                            String authorName, Integer categoryId, Integer isFinished, Integer status);

    /**
     * 修改novel
     */
    void updateArticles(Integer id, Articles articles, Long adminId);

    /**
     * 修改novel状态
     */
    void updateStatus(Integer id, Integer status);


    /**
     * 根据书籍ID删除整本书
     */
    void logicalDeleteArticleById(Integer articleId, Long adminId);

    /**
     * 批量删除书籍
     */
    void batchLogicalDeleteArticles(List<Integer> articleIds, Long adminId);


    /**
     * 插入新的novel
     * @param articles articles
     */
    Integer insertArticles(Articles articles, Long adminId);

    /**
     * 文本导入 .txt
     */
    Integer importTxt(Articles articles, MultipartFile file, Long adminId);


    /**
     * 单本恢复
     */
    void recoverArticleById(Integer articleId, Long adminId);

    /**
     * 批量恢复
     */
    void batchRecoverArticles(List<Integer> articleIds, Long adminId);

    PageInfo<ArticleRecycleListDTO> queryDeletedArticles(int pageNum, int pageSize);

    /**
     * 预解析
     * @param file 文件
     * @return 预解析结果
     */
    TxtImportPreviewResponse parseTxtPreview(MultipartFile file);
}
