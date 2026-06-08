package com.kxhy.controller;


import com.kxhy.domain.po.Categories;
import com.kxhy.service.CategoriesService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping("categoryList")
    public Result<List<Categories>> getCategories() {
        List<Categories> categories = categoriesService.getCategories();
        return Result.success(categories);
    }

    @GetMapping("queryCategory")
    public Result<Categories> queryCategory(@RequestParam String name) {
        Categories categories = categoriesService.queryCategories(name);
        return Result.success(categories);
    }

    @RequirePermission("admin:category:status")
    @PutMapping("modifyStatus/{id}")
    public Result<Void> modifyStatus(@PathVariable Integer id, @RequestBody Categories categories) {
        categoriesService.modifyStatus(id, categories);
        return Result.success();
    }

    @RequirePermission("admin:category:add")
    @PostMapping("addCategory")
    public Result<Void> addCategory(@RequestParam String name, @RequestParam String summary) {
        categoriesService.insertCategory(name, summary);
        return Result.success();
    }

    @RequirePermission("admin:category:delete")
    @DeleteMapping("{id}/deleteCategory")
    public Result<Void> deleteCategory(@PathVariable Integer id) {
        categoriesService.deleteCategory(id);
        return Result.success();
    }

    @RequirePermission("admin:category:update")
    @PutMapping("{id}/summary")
    public Result<Void> updateCategory(@PathVariable Integer id, @RequestBody Categories categories) {
        categoriesService.updateCategory(id, categories);
        return Result.success();
    }

}
