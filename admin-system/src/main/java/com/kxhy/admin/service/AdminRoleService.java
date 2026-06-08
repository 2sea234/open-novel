package com.kxhy.admin.service;

import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.role.AdminRoleMenuAssignDTO;
import com.kxhy.admin.domain.dto.role.AdminRolePermissionDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleAddDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleQueryDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleStatusDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleUpdateDTO;
import com.kxhy.admin.domain.dto.user.AdminUserRoleAssignDTO;
import com.kxhy.admin.domain.vo.AdminRoleListVO;

import java.util.List;

public interface AdminRoleService {

    PageInfo<AdminRoleListVO> pageRoles(Integer pageNum, Integer pageSize, AdminRoleQueryDTO query);

    void addRole(AdminRoleAddDTO dto, Long adminId, String username);

    void updateRole(Long id, AdminRoleUpdateDTO dto, Long adminId, String username);

    void updateRoleStatus(Long id, AdminRoleStatusDTO dto, Long adminId, String username);

    void deleteRole(Long id, Long adminId, String username);

    /**
     * 分配角色
     * @param userId 用户id
     * @param dto dto
     * @param adminId 管理员id
     */
    void assignRole(Long userId, AdminUserRoleAssignDTO dto, Long adminId, String username);

    /**
     * 获取用户角色id
     * @param userId 用户id
     * @return 角色id
     */
    List<Long> queryUserRoleIds(Long userId);
}
