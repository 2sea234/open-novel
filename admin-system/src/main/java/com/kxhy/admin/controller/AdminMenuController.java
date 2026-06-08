package com.kxhy.admin.controller;

import com.kxhy.admin.domain.dto.menu.AdminMenuAddDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuStatusDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuUpdateDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleMenuAssignDTO;
import com.kxhy.admin.domain.vo.AdminMenuTreeVO;
import com.kxhy.admin.service.AdminMenuService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 菜单管理
 * @Author: kxhy
 * @Date: 2026/6/2
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("adminSystem/menu")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    @RequirePermission("admin:menu:list")
    @GetMapping("tree")
    public Result<List<AdminMenuTreeVO>> queryMenuTree() {

        List<AdminMenuTreeVO> adminMenuTreeVOS = adminMenuService.queryAllMenuTree();

        return Result.success(adminMenuTreeVOS);
    }

    @RequirePermission("admin:menu:add")
    @PostMapping
    public Result<Void> addMenu(@RequestBody AdminMenuAddDTO dto,
                                @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                @RequestHeader(value = "X-Admin-Username", required = false) String username) {

        adminMenuService.addMenu(dto, adminId, username);

        return Result.success();
    }

    @RequirePermission("admin:menu:update")
    @PutMapping("{id}")
    public Result<Void> modifyMenu(@PathVariable("id") Long id,
                                   @RequestBody AdminMenuUpdateDTO dto,
                                   @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                   @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        adminMenuService.updateMenu(id, dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:menu:status")
    @PutMapping("{id}/status")
    public Result<Void> modifyMenuStatus(@PathVariable("id") Long id,
                                         @RequestBody AdminMenuStatusDTO dto,
                                         @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                         @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        adminMenuService.modifyMenuStatus(id, dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:menu:delete")
    @DeleteMapping("{id}")
    public Result<Void> deleteMenu(@PathVariable("id") Long id,
                                   @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                   @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        adminMenuService.delMenu(id, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:role:assign-menu")
    @GetMapping("{id}/menus")
    public Result<List<Long>> queryRoleMenuIds(@PathVariable Long id) {
        return Result.success(adminMenuService.queryRoleMenuIds(id));
    }

    @RequirePermission("admin:role:assign-menu")
    @PutMapping("{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody AdminRoleMenuAssignDTO dto, @RequestHeader("X-Admin-Id") Long adminId) {
        adminMenuService.assignMenus(id, dto, adminId);
        return Result.success();
    }

}
