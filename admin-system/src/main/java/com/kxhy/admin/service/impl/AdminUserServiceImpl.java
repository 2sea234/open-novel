package com.kxhy.admin.service.impl;

import com.kxhy.admin.domain.dto.user.AdminUserAddDTO;
import com.kxhy.admin.domain.dto.user.AdminUserRoleAssignDTO;
import com.kxhy.admin.domain.dto.user.AdminUserStatusDTO;
import com.kxhy.admin.domain.dto.user.AdminUserUpdateDTO;
import com.kxhy.admin.domain.po.AdminUser;
import com.kxhy.admin.mapper.AdminRoleMapper;
import com.kxhy.admin.mapper.AdminUserMapper;
import com.kxhy.admin.mapper.AdminUserRoleMapper;
import com.kxhy.admin.service.AdminOperationLogService;
import com.kxhy.admin.service.AdminUserService;
import com.kxhy.admin.utils.OperationLogDescUtil;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    @Value("${admin.user.default-password:123456}")
    private String defaultPassword;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AdminUserMapper adminUserMapper;
    private final AdminOperationLogService adminOperationLogService;
    private final AdminUserRoleMapper adminUserRoleMapper;

    /**
     * 添加管理员
     * @param dto dto
     * @param adminId 管理员id
     */
    @Override
    public void addAdminUser(AdminUserAddDTO dto, Long adminId, String username) {
        String operationDesc = "新增用户：" + dto.getNickname();
        try {
            if (adminId == null) {
                throw new BizException(401, "当前管理员未登录");
            }

            if (dto == null) {
                throw new BizException(400, "参数不能为空");
            }

            if (dto.getUsername() == null || dto.getUsername().isBlank()) {
                throw new BizException(400, "请输入用户名,参数不能为空");
            }

            if (dto.getNickname() == null || dto.getNickname().isBlank()) {
                throw new BizException(400, "请输入昵称,参数不能为空");
            }

            if (dto.getStatus() == null) {
                dto.setStatus(1);
            }

            if (dto.getStatus() < 0 || dto.getStatus() > 1) {
                throw new BizException(400, "用户状态不合法");
            }

            Integer count = adminUserMapper.countByUsername(dto.getUsername());
            if (count != null && count > 0) {
                throw new BizException(400, "用户名已存在");
            }

            AdminUser adminUser = new AdminUser();
            adminUser.setUsername(dto.getUsername().trim());
            adminUser.setNickname(dto.getNickname().trim());
            adminUser.setStatus(dto.getStatus());
            String encodePassword = passwordEncoder.encode(defaultPassword);
            adminUser.setPassword(encodePassword);
            adminUser.setCreateBy(adminId);
            adminUser.setUpdateBy(adminId);

            Integer i = adminUserMapper.insertAdminUser(adminUser);
            if (i == null || i < 0) {
                throw new BizException(500, "新增管理员失败");
            }
            adminOperationLogService.recordSuccess(
                    "用户管理",
                    "新增",
                    operationDesc,
                    adminId,
                    username);
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "用户管理",
                    "新增",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage());
            throw e;
        }
    }

    /**
     * 修改管理员信息
     * @param id id
     * @param dto dto
     * @param adminId 管理员id
     */
    @Override
    public void modifyAdminUser(Long id, AdminUserUpdateDTO dto, Long adminId, String username) {

        List<String> changes = new ArrayList<>();

        AdminUser adminUser = adminUserMapper.listAdminUser(id);

        OperationLogDescUtil.addChange(
                changes,
                "昵称",
                adminUser.getNickname(),
                dto.getNickname()
        );

        OperationLogDescUtil.addChange(
                changes,
                "状态",
                adminUser.getStatus(),
                dto.getStatus(),
                status -> status != null && status == 1 ? "启用" : "禁用"
        );

        String operationDesc = OperationLogDescUtil.buildUpdateDesc("用户", id, changes);

        try {
            if (id == null) {
                throw new BizException(400, "id不能为空");
            }

            if (dto.getNickname() == null || dto.getNickname().isBlank()) {
                throw new BizException(400, "昵称不能为空");
            }

            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BizException(400, "用户状态值不合法");
            }

            if (id.equals(adminId) && dto.getStatus() == 0) {
                throw new BizException(400, "禁止禁用当前登录帐号");
            }

            if (id == 1L && dto.getStatus() == 0) {
                throw new BizException(400, "不能禁内置前管理员");
            }
            Integer i = adminUserMapper.updateAdminUser(id, dto, adminId);
            if (i == null || i <= 0) {
                throw new BizException(404, "管理员不存在或已删除");
            }
            adminOperationLogService.recordSuccess(
                    "用户管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "用户管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }
    }

    /**
     * 修改管理员状态
     * @param id id
     * @param dto 状态
     * @param adminId 管理员id
     */
    @Override
    public void modifyAdminUserStatus(Long id, AdminUserStatusDTO dto, Long adminId, String username) {

        List<String> changes = new ArrayList<>();

        AdminUser adminUser = adminUserMapper.listAdminUser(id);
        if (!Objects.equals(adminUser.getStatus(), dto.getStatus())) {
            changes.add("用户名：" + adminUser.getUsername() + "状态：" +
                    (adminUser.getStatus() == 0 ? "禁用" : "启用") +
                    " -> " + (dto.getStatus() == 0 ? "禁用" : "启用"));
        }

        String operationDesc = "用户修改状态，修改用户id：" + id + "，修改内容：" + String.join(";", changes);

        try {
            if (adminId == null) throw new BizException(401, "管理员未登录");

            if (id == null) throw new BizException(400, "id 不能为空");

            if (dto == null || dto.getStatus() == null) throw new BizException(400, "状态不能为空");

            int status = dto.getStatus();

            if (status != 0 && status != 1) throw new BizException(400, "用户状态不合法");

            if (id.equals(adminId) && status == 0) throw new BizException(400, "禁止禁用当前登录账号");

            if (id== 1L && status == 0) throw new BizException(400, "禁止禁用内置超级管理员");

            Integer rows = adminUserMapper.updateAdminUserStatus(id, status, adminId);
            if (rows == null || rows <= 0) throw new BizException(500, "该管理员不存在或已删除");
            adminOperationLogService.recordSuccess(
                    "用户管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "用户管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }

    }

    /**
     * 重置管理员密码
     * @param id id
     * @param adminId 管理员id
     */
    @Override
    public void resetAdminUserPassword(Long id, Long adminId, String username) {

        String operationDesc = "用户id：" + id + "密码已被重置";

        try {
            if (adminId == null) {
                throw new BizException(400, "未登录");
            }

            if (id == null) {
                throw new BizException(400, "id不能为空");
            }



            String encode = passwordEncoder.encode(defaultPassword);
            Integer rows = adminUserMapper.resetAdminUserPassword(id, encode, adminId);
            if (rows == null || rows <= 0) {
                throw new BizException(404, "管理员不存在或已删除");
            }
            adminOperationLogService.recordSuccess(
                    "用户管理",
                    "重置密码",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "用户管理",
                    "重置密码",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }

    }

    /**
     * 删除管理员
     * @param id id
     * @param adminId 管理员id
     */
    @Override
    public void deleteAdminUser(Long id, Long adminId, String username) {

        String operationDesc = "用户id：" + id + "已被删除";

        try {
            if (adminId == null) {
                throw new BizException(400, "未登录");
            }

            if (id == null) {
                throw new BizException(400, "id不能为空");
            }

            if(id.equals(adminId)) {
                throw new BizException(400, "不能删除当前登录帐号");
            }

            if (id == 1L) {
                throw new BizException(400, "不能删除内置超级管理员");
            }

            AdminUser adminUsers = adminUserMapper.selectUserById(id);
            if (adminUsers == null) {
                throw new BizException(400, "该用户已删除或不存在");
            }

            operationDesc = "删除用户，用户id：" + id
                    + "，账号：" + adminUsers.getUsername()
                    + "，昵称：" + adminUsers.getNickname();

            Integer rows = adminUserMapper.deleteAdminUser(id, adminId);
            if (rows == null || rows <= 0) {
                throw new BizException(404, "管理员不存在或已删除");
            }

            adminUserRoleMapper.deleteRolesByUserId(id);

            adminOperationLogService.recordSuccess(
                    "用户管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "用户管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }
    }




}
