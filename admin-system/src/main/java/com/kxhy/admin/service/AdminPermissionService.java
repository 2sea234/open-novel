package com.kxhy.admin.service;

import com.kxhy.admin.domain.dto.role.AdminRolePermissionDTO;
import com.kxhy.admin.domain.vo.AdminPermissionVO;

import java.util.List;

public interface AdminPermissionService {

    List<String> queryPermissionCodesByAdminId(Long adminId);

    List<AdminPermissionVO> queryPermissionList();

    List<Long> queryRolePermissionIds(Long roleId);

    void assignPermissions(Long roleId, AdminRolePermissionDTO dto, Long adminId);


}
