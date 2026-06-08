package com.kxhy.mapper;

import com.kxhy.domain.dto.ArticleRecycleListDTO;
import com.kxhy.domain.dto.ArticlesDTO;
import com.kxhy.domain.po.Articles;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticlesMapper {

    /**
     * 查询所有novel数量
     */
    @Select("SELECT COUNT(*) from articles")
    int count();

    /**
     * 查询所有novel
     * @return novel列表
     */
    @Select("""
            select a.id,
                a.title,
                a.author_name as authorName,
                c.name as categoryName,
                a.cover_image as coverImage,
                a.summary,
                a.word_count as wordCount,
                a.chapter_count as chapterCount,
                a.status,
                a.update_time as updateTime,
                a.is_finished as isFinished,
                case a.is_finished
                    when 0 then '封禁'
                    when 1 then '完结'
                    when 2 then '连载'
                    when 3 then '停更'
                    else '未知'
                end as isFinishedDesc                   
            from articles a
            inner join categories c on c.id = a.category_id and is_deleted = 0
            """)
    List<ArticlesDTO> getArticles();

    /**
     * 根据id查询novel
     */
    ArticlesDTO queryArticleById(@Param("articleId") Integer articleId);



    /**
     * 多条件查询
     */
    List<ArticlesDTO> queryArticles(@Param("title") String title,
                                    @Param("authorName") String authorName,
                                    @Param("categoryId") Integer categoryId,
                                    @Param("isFinished") Integer isFinished,
                                    @Param("status") Integer status);

    /**
     * 修改novel
     */
    Integer updateArticles(@Param("id") Integer id, @Param("articles") Articles articles);

    /**
     * 修改novel状态
     */
    @Update("update articles set status = #{status} where id = #{id}")
    Integer updateIsStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 添加novel
     * @param articles novel
     * @return 添加成功：1, 添加失败：0
     */
    Integer insertArticle(Articles articles);

    Integer updateArticleStats(Integer id, Integer wordCount, Integer chapterCount);


    /**
     * 逻辑删除novel
     * @param articleId novel id
     * @param deletedBy 删除人
     * @return 删除成功：1, 删除失败：0
     */
    int logicalDeleteArticleById(@Param("articleId") Integer articleId, @Param("adminId") Long adminId);

    /**
     * 批量删除novel
     */
    int batchLogicalDeleteArticles(@Param("articleIds") List<Integer> articleIds,
                            @Param("adminId") Long adminId);


    /**
     * 根据novel id查询novel
     */
    int countNotDeletedArticlesByIds(@Param("articleIds") List<Integer> articleIds);

    /**
     * 根据novel id查询章节
     */
    List<Map<String, Object>> selectChaptersByArticleId(@Param("articleId") Integer articleId);

    /**
     * 根据章节id查询章节内容
     */
    List<Map<String, Object>> selectChapterContentsByChapterIds(@Param("chapterIds") List<Integer> chapterIds);

    /**
     * 按 id 查询文章（包含已删除）
     */
    Articles selectArticleByIdIncludeDeleted(@Param("id") Integer id);

    /**
     * 批量查询文章删除状态
     */
    List<Articles> selectArticleDeleteStatusByIds(@Param("articleIds") List<Integer> articleIds);

    /**
     * 统计已删除的novel数量
     */
    int countDeletedArticlesByIds(@Param("articleIds") List<Integer> articleIds);

    /**
     * 恢复novel
     */
    int recoverArticleById(@Param("id") Integer id, @Param("adminId") Long adminId);

    /**
     * 批量恢复novel
     */
    int batchRecoverArticles(List<Integer> articleIds, @Param("adminId") Long adminId);

    /**
     * 查询已删除的novel
     */
    List<ArticleRecycleListDTO> queryRecycleArticles();

}
