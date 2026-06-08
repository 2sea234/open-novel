package com.kxhy.mapper;

import com.kxhy.domain.po.Categories;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoriesMapper {


    /**
     * 查询所有categories
     * @return categories列表
     */
    @Select("select id, name, status, summary, creat_time createTime from categories")
    List<Categories> getCategories();

    /**
     * 根据name查询categories
     * @param name id
     * @return categories
     */
    @Select("select id, name, status, summary, creat_time createTime from categories where name = #{name}")
    Categories byNameQueryCategories(@Param("name") String name);

    /**
     * 插入新的类型
     * @param name 类型名称
     * @return 0, 1
     */
    @Insert("insert into categories(`name`, status, creat_time, summary, is_delete)values (#{name}, 1, now(), #{summary}, 0)")
    Integer insertCategory(@Param("name") String name, @Param("summary") String summary);

    /**
     * 修改分类状态
     * @param id 分类id
     * @return 0, 1
     */
    Integer updateCategoryStatus(@Param("id") Integer id, @Param("categories") Categories categories);

    /**
     * 删除分类
     * @param id 分类id
     * @return 0, 1
     */
    int categoryDeleteById(@Param("id") Integer id);

    Integer countNormalCategory(@Param("id") Integer id);

    int updateCategory(@Param("id") Integer id, @Param("categories") Categories categories);
}
