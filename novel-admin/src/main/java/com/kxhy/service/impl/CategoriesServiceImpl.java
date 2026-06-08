package com.kxhy.service.impl;

import com.kxhy.domain.po.Categories;
import com.kxhy.mapper.CategoriesMapper;
import com.kxhy.service.CategoriesService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CategoriesServiceImpl implements CategoriesService {


    private final CategoriesMapper categoriesMapper;

    @Override
    public List<Categories> getCategories() {

        List<Categories> categories = categoriesMapper.getCategories();
        return categories;
    }

    @Override
    public Categories queryCategories(String name) {
        return categoriesMapper.byNameQueryCategories(name);
    }

    @Override
    public void modifyStatus(Integer id, Categories categories) {

        if (id == null) {
            throw new BizException(400, "分类ID不能为空");
        }

        if (categories == null) {
            throw new BizException(400, "不能为空");
        }

        Integer integer = categoriesMapper.updateCategoryStatus(id, categories);
        if (integer <= 0) {
            throw new BizException(404, "分类不存在，修改失败");
        }


    }

    @Override
    public void insertCategory(String name, String summary) {
        if (name == null) {
            throw new BizException(400, "分类名称不能为空");
        }

        Integer integer = categoriesMapper.insertCategory(name, summary);
        if (integer <= 0) {
            throw new BizException(400, "分类已存在");
        }
    }

    @Override
    public void deleteCategory(Integer id) {

        if (id == null) {
            throw new BizException(400, "分类ID不能为空");
        }

        Integer countCategory = categoriesMapper.countNormalCategory(id);

        if (countCategory == null || countCategory <= 0) {
            throw new BizException(404, "分类不存在或已删除");
        }

        int rows = categoriesMapper.categoryDeleteById(id);
        if (rows <= 0) {
            throw new BizException(500, "删除失败");
        }
    }

    @Override
    public void updateCategory(Integer id, Categories categories) {

        if (id == null) {
            throw new BizException(400, "分类ID不能为空");
        }

        if (categories == null) {
            throw new BizException(400, "参数不能为空");
        }

        if (categories.getSummary() == null || categories.getSummary().isBlank()) {
            throw new BizException(400, "分类描述不能为空");
        }

        Integer countCategory = categoriesMapper.countNormalCategory(id);
        if (countCategory == null || countCategory <= 0) {
            throw new BizException(404, "分类不存在或已删除");
        }

        int rows = categoriesMapper.updateCategory(id, categories);
        if (rows <= 0) {
            throw new BizException(500, "修改失败");
        }

    }
}
