package com.kxhy.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.role.AdminRoleMenuAssignDTO;
import com.kxhy.admin.domain.dto.role.AdminRolePermissionDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleAddDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleQueryDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleStatusDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleUpdateDTO;
import com.kxhy.admin.domain.dto.user.AdminUserRoleAssignDTO;
import com.kxhy.admin.domain.po.AdminRole;
import com.kxhy.admin.domain.vo.AdminRoleListVO;
import com.kxhy.admin.mapper.*;
import com.kxhy.admin.service.AdminOperationLogService;
import com.kxhy.admin.service.AdminRoleService;
import com.kxhy.admin.utils.OperationLogDescUtil;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {

    private final AdminRoleMapper adminRoleMapper;
    private final AdminRoleMenuMapper adminRoleMenuMapper;
    private final AdminMenuMapper adminMenuMapper;
    private final AdminPermissionMapper adminPermissionMapper;
    private final AdminRolePermissionMapper adminRolePermissionMapper;
    private final AdminOperationLogService adminOperationLogService;
    private final AdminUserRoleMapper adminUserRoleMapper;
    private final AdminUserMapper adminUserMapper;

    /**
     * 分页查询角色列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param query 查询条件
     * @return 角色列表
     */
    @Override
    public PageInfo<AdminRoleListVO> pageRoles(Integer pageNum, Integer pageSize, AdminRoleQueryDTO query) {
        if (pageNum == null || pageNum <= 0) {
            throw new BizException(400, "页码参数不合法");
        }

        if (pageSize == null || pageSize <= 0) {
            throw new BizException(400, "每页条数参数不合法");
        }

        PageHelper.startPage(pageNum, pageSize);
        List<AdminRoleListVO> adminRoleListVOS = adminRoleMapper.selectRolePage(query);
        return new PageInfo<>(adminRoleListVOS);
    }

    /**
     * 添加角色
     * @param dto 角色信息
     * @param adminId 当前管理员id
     */
    @Override
    public void addRole(AdminRoleAddDTO dto, Long adminId, String username) {

        String operationDesc = "角色昵称" + (dto == null ? null : dto.getRoleName());

        try {
            if (adminId ==  null) throw new BizException(401, "当前管理员未登录");

            if (dto ==  null) throw new BizException(400, "角色信息不能为空");

            if (dto.getRoleCode() == null || dto.getRoleCode().isBlank()) {
                throw new BizException(400, "角色编码不能为空");
            }

            String roleCode = dto.getRoleCode().trim();

            if (!roleCode.matches("^[a-z0-9_]+$")) throw new BizException(400, "角色编码只能包含小写字母、数字和下划线");

            if (dto.getRoleName() == null || dto.getRoleName().isBlank()) {
                throw new BizException(400, "角色名称不能为空");
            }

            // 添加角色之前需先检查角色编码是否重复
            Integer countRoleCode = adminRoleMapper.countByRoleCode(roleCode);
            if (countRoleCode != null && countRoleCode > 0) throw new BizException(400, "角色编码已存在");

            // 在检查状态是否合法，就是有没有被禁用或删除
            int status = dto.getStatus() == null ? 1 : dto.getStatus();
            if (status != 0 && status != 1) throw new BizException(400, "角色状态不合法");

            // 排序
            int sort = dto.getSort() == null ? 0 : dto.getSort();

            AdminRole role = new AdminRole();
            role.setRoleCode(roleCode);
            role.setRoleName(dto.getRoleName().trim());
            role.setStatus(status);
            role.setSort(sort);
            role.setRemark(dto.getRemark());
            role.setUpdateBy(adminId);
            role.setCreateBy(adminId);

            Integer rows = adminRoleMapper.insertRole(role);
            if (rows == null || rows <= 0) throw new BizException(500, "添加角色失败");
            adminOperationLogService.recordSuccess(
                    "角色管理",
                    "新增",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "角色管理",
                    "新增",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }


    }

    /**
     * 更新角色
     * @param id 角色id
     * @param dto 角色信息
     * @param adminId 当前管理员id
     */
    @Override
    public void updateRole(Long id, AdminRoleUpdateDTO dto, Long adminId, String username) {

        String operationDesc = "更新角色，角色id" + id + "，角色名称" + (dto == null ? null : dto.getRoleName());

        List<String> changes = new ArrayList<>();


        try {
            if (adminId == null) throw new BizException(401, "当前管理员未登录");

            if (id == null) throw new BizException(400, "角色id不能为空");

            if (dto == null) throw new BizException(400, "角色信息不能为空");

            if (dto.getRoleName() == null || dto.getRoleName().isBlank()) throw new BizException(400, "角色名称不能为空");

            if (dto.getStatus() == null) throw new BizException(400, "角色状态不能为空");

            if (dto.getStatus() != 0 && dto.getStatus() != 1) throw new BizException(400, "角色状态不合法");

            if (dto.getSort() == null) {
                dto.setSort(0);
            }

            AdminRole role = adminRoleMapper.selectRoleById(id);

            if (role == null) throw new BizException(400, "角色不存在或已删除");

            if ("super_admin".equals(role.getRoleCode()) && dto.getStatus() == 0) throw new BizException(400, "内置超级管理员角色不能禁用");

            dto.setRemark(dto.getRoleName().trim());

            Integer rows = adminRoleMapper.updateRoleById(id, dto, adminId);
            if (rows == null || rows <= 0) throw new BizException(500, "更新角色失败");

            adminOperationLogService.recordSuccess(
                    "角色管理",
                    "更新",
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "角色管理",
                    "更新",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }

    }

    /**
     * 更新角色状态
     * @param id 角色id
     * @param dto 角色状态
     * @param adminId 当前管理员id
     */
    @Override
    public void updateRoleStatus(Long id, AdminRoleStatusDTO dto, Long adminId, String username) {

        String operationDesc = "更新角色状态，角色id" + id + "，角色状态" + (dto == null ? null : dto.getStatus());

        try {
            if (adminId == null) throw new BizException(401, "当前管理员登录");

            if (id == null) throw new BizException(400,  "角色id不能为空");

            if (dto == null || dto.getStatus() == null) throw new BizException(400, "角色状态不能为空");

            Integer status = dto.getStatus();

            if (status != 0 && status != 1) throw new BizException(400, "角色状态不合法");

            // 先检查角色是否存在
            AdminRole role = adminRoleMapper.selectRoleById(id);
            if (role == null) throw new BizException(400, "角色不存在或已删除");

            // 如果是超级管理员角色，则不能禁用
            if ("super_admin".equals(role.getRoleCode()) && status == 0) throw new BizException(400, "内置超级管理员角色不能禁用");

            // 然后在进行状态的修改
            Integer rows = adminRoleMapper.updateRoleStatusById(id, status, adminId);
            if (rows == null || rows <= 0) throw new BizException(500, "更新角色状态失败");
            adminOperationLogService.recordSuccess(
                    "角色管理",
                    "更新",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "角色管理",
                    "更新",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }
    }

    @Override
    public void deleteRole(Long id, Long adminId, String username) {
        String operationDesc = "删除角色，角色id:" + id;
        try {
            if (adminId == null) throw new BizException(401, "当前管理员未登录");

            if (id == null) throw new BizException(400, "角色id不能为空");

            AdminRole role = adminRoleMapper.selectRoleById(id);
            if (role ==  null) throw new BizException(400, "角色不存在或已删除");

            if ("super_admin".equals(role.getRoleCode())) throw new BizException(400, "内置超级管理员角色不能删除");

            Integer bindCount = adminRoleMapper.countUserRoleByRoleId(id);
            if (bindCount != null && bindCount > 0) throw new BizException(400, "该角色已绑定管理员，请先解除绑定后删除");

            Integer rows = adminRoleMapper.logicalDeleteRole(id, adminId);
            if (rows == null || rows <= 0) throw new BizException(500, "删除角色失败");
            adminOperationLogService.recordSuccess(
                    "角色管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "角色管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }


    }

    /**
     * 分配角色
     * @param userId 用户id
     * @param dto dto
     * @param adminId 管理员id
     */
    @Override
    public void assignRole(Long userId, AdminUserRoleAssignDTO dto, Long adminId, String username) {

        String operationDesc = "分配用户角色，用户id：" + userId;

        try {
            if (adminId == null) {
                throw new BizException(401, "当前管理员未登录");
            }

            if (userId == null) {
                throw new BizException(400, "管理员id不能为空");
            }

            if (dto == null || dto.getRoleIds() == null) {
                throw new BizException(400, "角色列表不能为空");
            }

            // 禁止修改自己的角色，避免把自己权限改没
            if (userId.equals(adminId)) {
                throw new BizException(400, "不能为当前登录帐号分配角色");
            }

            // 保护内置超级管理员
            if (userId.equals(1L)) {
                throw new BizException(400, "不能为内置管理员分配角色");
            }

            Integer userCount = adminUserMapper.countNormalUserById(userId);


            if (userCount == null || userCount <= 0) {
                throw new BizException(400, "管理员不存在或已删除");
            }

            // 旧角色：必须在删除旧绑定之前查
            List<Long> oldRoleIds = adminUserRoleMapper.selectRoleIdsByUserId(userId);

            List<Long> roleIds = dto.getRoleIds()
                    .stream()
                    .distinct()
                    .toList();

            if (roleIds.contains(null)) {
                throw new BizException(400, "角色id不能为空");
            }

            if (!roleIds.isEmpty()) {
                Integer roleCount = adminRoleMapper.countAvailableRolesByIds(roleIds);
                if (roleCount == null || roleCount != roleIds.size()) {
                    throw new BizException(400, "存在无效或已禁用的角色");
                }
            }

            operationDesc = OperationLogDescUtil.buildAssignDesc(
                    "分配用户角色，用户id：" + userId,
                    "角色",
                    oldRoleIds,
                    roleIds
            );

            // 先删除旧角色绑定
            adminUserRoleMapper.deleteRolesByUserId(userId);

            // 非空才插入；空数组表示清空角色，但也要继续往下记成功日志
            if (!roleIds.isEmpty()) {
                adminUserRoleMapper.batchInsertRoles(userId, roleIds, adminId);
            }

            adminOperationLogService.recordSuccess(
                    "用户管理",
                    "分配角色",
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "用户管理",
                    "分配角色",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }
    }

    /**
     * 获取用户角色id
     * @param userId 用户id
     * @return 角色id
     */
    @Override
    public List<Long> queryUserRoleIds(Long userId) {

        if (userId == null) {
            throw new BizException(400, "管理员id不能为空");
        }

        Integer userCount = adminUserMapper.countNormalUserById(userId);
        if (userCount == null || userCount <= 0) {
            throw new BizException(404, "管理员不存在或已删除");
        }
        return adminUserRoleMapper.selectRoleIdsByUserId(userId);
    }
}
