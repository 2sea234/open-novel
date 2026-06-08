package com.kxhy.admin.controller;

import com.kxhy.admin.domain.dto.role.AdminRolePermissionDTO;
import com.kxhy.admin.domain.vo.AdminPermissionVO;
import com.kxhy.admin.service.AdminPermissionService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("adminSystem/permissions")
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    /**
     * 查询权限列表
     */
    @RequirePermission("admin:role:assign-permission")
    @GetMapping("list")
    public Result<List<AdminPermissionVO>> queryPermissionList() {
        return Result.success(adminPermissionService.queryPermissionList());
    }

    /**
     * 查询角色权限
     */
    @RequirePermission("admin:role:assign-permission")
    @GetMapping("{id}/permission")
    public Result<List<Long>> queryRolePermissionIds(@PathVariable Long id) {
        return Result.success(adminPermissionService.queryRolePermissionIds(id));
    }

    /**
     * 分配权限
     */
    @RequirePermission("admin:role:assign-permission")
    @PutMapping("{id}/permission")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody AdminRolePermissionDTO dto, @RequestHeader("X-Admin-Id") Long adminId) {
        adminPermissionService.assignPermissions(id, dto, adminId);
        return Result.success();
    }

}
