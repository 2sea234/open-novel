package com.kxhy.admin.controller;

import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.role.AdminRoleMenuAssignDTO;
import com.kxhy.admin.domain.dto.role.AdminRolePermissionDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleAddDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleQueryDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleStatusDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleUpdateDTO;
import com.kxhy.admin.domain.dto.user.AdminUserRoleAssignDTO;
import com.kxhy.admin.domain.vo.AdminRoleListVO;
import com.kxhy.admin.service.AdminRoleService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("adminSystem/roles")
public class AdminRoleController {

    private final AdminRoleService roleService;

    @RequirePermission("admin:role:list")
    @GetMapping("page")
    public Result<PageInfo<AdminRoleListVO>> queryRolePage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           AdminRoleQueryDTO query) {
        PageInfo<AdminRoleListVO> adminRoleListVOPageInfo = roleService.pageRoles(pageNum, pageSize, query);
        return Result.success(adminRoleListVOPageInfo);
    }

    @RequirePermission("admin:role:add")
    @PostMapping("addRole")
    public Result<Void> addRole(@RequestBody AdminRoleAddDTO dto,
                                @RequestHeader("X-Admin-Id") Long adminId,
                                @RequestHeader("X-Admin-Username") String username) {
        roleService.addRole(dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:role:update")
    @PutMapping("{id}/update")
    public Result<Void> updateRole(@PathVariable Long id,
                                   @RequestBody AdminRoleUpdateDTO dto,
                                   @RequestHeader("X-Admin-Id") Long adminId,
                                   @RequestHeader("X-Admin-Username") String username) {
        roleService.updateRole(id, dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:role:status")
    @PutMapping("{id}/status")
    public Result<Void> updateRoleStatus(@PathVariable Long id,
                                         @RequestBody AdminRoleStatusDTO dto,
                                         @RequestHeader("X-Admin-Id") Long adminId,
                                         @RequestHeader("X-Admin-Username") String username) {
        roleService.updateRoleStatus(id, dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:role:delete")
    @DeleteMapping("{id}/delete")
    public Result<Void> deleteRole(@PathVariable Long id,
                                   @RequestHeader("X-Admin-Id") Long adminId,
                                   @RequestHeader("X-Admin-Username") String username) {
        roleService.deleteRole(id, adminId, username);
        return Result.success();
    }


    /**
     * 分配角色
     * @param id id
     * @param dto dto
     * @param adminId 管理员id
     * @return result
     */
    @RequirePermission("admin:user:assign-role")
    @PutMapping("{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id,
                                    @RequestBody AdminUserRoleAssignDTO dto,
                                    @RequestHeader("X-Admin-Id") Long adminId,
                                    @RequestHeader("X-Admin-Username") String username) {
        roleService.assignRole(id, dto, adminId, username);
        return Result.success();
    }

    /**
     * 获取管理员角色
     * @param id id
     * @return result
     */
    @RequirePermission("admin:user:list")
    @GetMapping("{id}/roles")
    public Result<List<Long>> getRole(@PathVariable Long id) {
        return Result.success(roleService.queryUserRoleIds(id));
    }

}
