package com.kxhy.service;

import com.kxhy.domain.po.Categories;
import com.opennovel.common.result.Result;

import java.util.List;


public interface CategoriesService {

    /**
     * 查询所有categories
     * @return categories列表
     */
    List<Categories> getCategories();

    /**
     * 根据name查询categories
     *
     * @param name name
     * @return categories
     */
    Categories queryCategories(String name);

    /**
     * 修改Status状态
     * @param id Status id
     * @param status Status状态
     * @return 修改结果
     */
    void modifyStatus(Integer id, Categories categories);

    /**
     * 插入新的类型
     * @param name 类型名称
     * @return 插入结果
     */
    void insertCategory(String name, String summary);

    /**
     * 删除分类
     * @param id 分类id
     * @return 删除结果
     */
    void deleteCategory(Integer id);

    void updateCategory(Integer id, Categories categories);

}
