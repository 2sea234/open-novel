package com.kxhy.admin.service;

import com.kxhy.admin.domain.dto.user.AdminUserAddDTO;
import com.kxhy.admin.domain.dto.user.AdminUserRoleAssignDTO;
import com.kxhy.admin.domain.dto.user.AdminUserStatusDTO;
import com.kxhy.admin.domain.dto.user.AdminUserUpdateDTO;

import java.util.List;

public interface AdminUserService {

    /**
     * 添加管理员
     * @param adminUserAddDTO dto
     * @param adminId 管理员id
     */
    void addAdminUser(AdminUserAddDTO adminUserAddDTO, Long adminId, String username);

    /**
     * 修改管理员信息
     * @param id id
     * @param dto dto
     * @param adminId 管理员id
     */
    void modifyAdminUser(Long id, AdminUserUpdateDTO dto, Long adminId, String username);

    /**
     * 修改管理员状态
     * @param id id
     * @param status 状态
     * @param adminId 管理员id
     */
    void modifyAdminUserStatus(Long id, AdminUserStatusDTO status, Long adminId, String username);

    /**
     * 重置管理员密码
     * @param id id
     * @param password 密码
     * @param adminId 管理员id
     */
    void resetAdminUserPassword(Long id, Long adminId, String username);

    /**
     * 删除管理员
     * @param id id
     * @param isDeleteId 删除管理员id
     * @param adminId 管理员id
     */
    void deleteAdminUser(Long id, Long adminId, String username);





}
