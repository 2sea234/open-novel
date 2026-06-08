package com.kxhy.admin.service.impl;

import com.kxhy.admin.domain.dto.role.AdminRolePermissionDTO;
import com.kxhy.admin.domain.vo.AdminPermissionVO;
import com.kxhy.admin.mapper.AdminPermissionMapper;
import com.kxhy.admin.mapper.AdminRoleMapper;
import com.kxhy.admin.mapper.AdminRolePermissionMapper;
import com.kxhy.admin.service.AdminPermissionService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final AdminPermissionMapper adminPermissionMapper;
    private final AdminRolePermissionMapper adminRolePermissionMapper;
    private final AdminRoleMapper adminRoleMapper;

    @Override
    public List<String> queryPermissionCodesByAdminId(Long adminId) {

        if (adminId == null) {
            throw new BizException(401, "管理员未登录");
        }

        return adminPermissionMapper.selectPermissionCodesByAdminId(adminId);
    }

    @Override
    public List<AdminPermissionVO> queryPermissionList() {
        return adminPermissionMapper.selectPermissionList();
    }

    /**
     * 查询角色权限
     * @param roleId 角色id
     * @return 权限id列表
     */
    @Override
    public List<Long> queryRolePermissionIds(Long roleId) {

        if (roleId == null) {
            throw new BizException(400, "角色id不能为空");
        }

        Integer roleCount = adminRoleMapper.countUserRoleByRoleId(roleId);
        if (roleCount == null || roleCount <= 0) {
            throw new BizException(400, "角色不存在或已删除");
        }

        return adminRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    /**
     * 分配权限
     * @param roleId 角色id
     * @param dto 权限信息
     * @param adminId 当前管理员id
     */
    @Override
    public void assignPermissions(Long roleId, AdminRolePermissionDTO dto, Long adminId) {

        if (adminId == null) {
            throw new BizException(401, "当前管理员未登录");
        }

        if (roleId == null) {
            throw new BizException(400, "角色id不能为空");
        }

        if (dto == null || dto.getPermissionIds() == null) {
            throw new BizException(400, "权限列表不能为空");
        }

        Integer roleCount = adminRoleMapper.countUserRoleByRoleId(roleId);
        if (roleCount == null || roleCount <= 0) {
            throw new BizException(400, "角色不存在或已删除");
        }

        if (roleId.equals(1L)) {
            throw new BizException(400, "不能修改内置超级管理员权限");
        }

        List<Long> permissionIds = dto.getPermissionIds()
                .stream()
                .distinct()
                .toList();

        if (permissionIds.contains(null)) {
            throw new BizException(400, "权限id不能为空");
        }

        if (!permissionIds.isEmpty()) {
            Integer permissionCount = adminPermissionMapper.countNormalPermissionsByIds(permissionIds);
            if (permissionCount == null || permissionCount <= 0) {
                throw new BizException(400, "存在无效或已删除的权限");
            }
        }


        adminRolePermissionMapper.deletePermissionsByRoleId(roleId);

        if (permissionIds.isEmpty()) {
            return;
        }

        int rows = adminRolePermissionMapper.batchInsertRolePermissions(roleId, permissionIds, adminId);
        if (rows <= 0) {
            throw new BizException(500, "分配权限失败");
        }
    }

}
