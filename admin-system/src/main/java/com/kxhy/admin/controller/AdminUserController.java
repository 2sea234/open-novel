package com.kxhy.admin.controller;

import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.user.*;
import com.kxhy.admin.domain.vo.AdminMenuVO;
import com.kxhy.admin.domain.vo.AdminUserListVO;
import com.kxhy.admin.service.*;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("adminSystem/user")
public class AdminUserController {

    private final AdminMenuService adminMenuService;
    private final AdminPermissionService adminPermissionService;
    private final AdminUserPageService adminUserPageService;
    private final AdminUserService adminUserService;

    /**
     * 获取菜单
     * @param adminId 管理员id
     * @return 菜单
     */
    @GetMapping("menu")
    public Result<List<AdminMenuVO>> queryMenuList(@RequestHeader("X-Admin-Id") Long adminId) {
        List<AdminMenuVO> menuList = adminMenuService.queryMenuListByAdminId(adminId);
        return Result.success(menuList);
    }


    /**
     * 获取权限
     * @param adminId 管理员id
     * @return 权限
     */
    @GetMapping("permission")
    public Result<List<String>> queryPermissionCodesByAdminId(@RequestHeader("X-Admin-Id") Long adminId) {
        List<String> strings = adminPermissionService.queryPermissionCodesByAdminId(adminId);
        return Result.success(strings);
    }

    /**
     * 获取管理员列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param query 查询参数
     * @return 管理员列表
     */
    @RequirePermission("admin:user:list")
    @GetMapping("userPage")
    public Result<PageInfo<AdminUserListVO>> queryAdminUserPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                AdminUserQueryDTO query) {
        PageInfo<AdminUserListVO> adminUserListVOPageInfo = adminUserPageService.pageAdminUsers(pageNum, pageSize, query);
        return Result.success(adminUserListVOPageInfo);
    }

    /**
     * 新增管理员
     * @param adminUserAddDTO dto
     * @param adminId 管理员id
     * @return  Result
     */
    @RequirePermission("admin:user:add")
    @PostMapping("addAdminUser")
    public Result<Void> addAdminUser(@RequestBody AdminUserAddDTO adminUserAddDTO,
                                     @RequestHeader("X-Admin-Id") Long adminId,
                                     @RequestHeader("X-Admin-Username") String username) {
        adminUserService.addAdminUser(adminUserAddDTO, adminId, username);
        return Result.success();
    }

    /**
     * 修改管理员信息
     * @param id id
     * @param dto dto
     * @param adminId 管理员id
     * @return result
     */
    @RequirePermission("admin:user:update")
    @PutMapping("/{id}")
    public Result<Void> updateAdminUser(@PathVariable Long id,
                                        @RequestBody AdminUserUpdateDTO dto,
                                        @RequestHeader("X-Admin-Id") Long adminId,
                                        @RequestHeader("X-Admin-Username") String username) {
        adminUserService.modifyAdminUser(id, dto, adminId, username);
        return Result.success();
    }

    /**
     * 修改管理员状态
     * @param id id
     * @param dto dto
     * @param adminId 管理员id
     * @return result
     */
    @RequirePermission("admin:user:status")
    @PutMapping("/{id}/status")
    public Result<Void> updateAdminUserStatus(@PathVariable Long id,
                                              @RequestBody AdminUserStatusDTO dto,
                                              @RequestHeader("X-Admin-Id") Long adminId,
                                              @RequestHeader("X-Admin-Username") String username) {
        adminUserService.modifyAdminUserStatus(id, dto, adminId, username);
        return Result.success();
    }

    /**
     * 重置管理员密码
     * @param id id
     * @param adminId 管理员id
     * @return result
     */
    @RequirePermission("admin:user:reset-password")
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetAdminUserPassword(@PathVariable Long id,
                                               @RequestHeader("X-Admin-Id") Long adminId,
                                               @RequestHeader("X-Admin-Username") String username) {
        adminUserService.resetAdminUserPassword(id, adminId, username);
        return Result.success();
    }

    /**
     * 删除管理员
     * @param id id
     * @param adminId 管理员id
     * @return result
     */
    @RequirePermission("admin:user:delete")
    @DeleteMapping("{id}/delete/user")
    public Result<Void> deleteAdminUser(@PathVariable Long id,
                                        @RequestHeader("X-Admin-Id") Long adminId,
                                        @RequestHeader("X-Admin-Username") String username) {
        adminUserService.deleteAdminUser(id, adminId, username);
        return Result.success();
    }

}
