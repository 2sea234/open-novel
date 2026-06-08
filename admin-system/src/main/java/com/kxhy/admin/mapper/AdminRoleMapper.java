package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.role.AdminRoleQueryDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleUpdateDTO;
import com.kxhy.admin.domain.po.AdminRole;
import com.kxhy.admin.domain.vo.AdminRoleListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminRoleMapper {

    // 查询角色列表接口 - 1. 先查角色编码 2. 在查询角色列表
    Integer countAvailableRolesByIds(@Param("roleIds")List<Long> roleIds);
    List<AdminRoleListVO> selectRolePage(@Param("query") AdminRoleQueryDTO query);

    // 添加角色接口 - 1. 先查角色编码 2. 在添加角色
    Integer countByRoleCode(@Param("roleCode") String roleCode);
    Integer insertRole(AdminRole role);

    // 修改角色接口 - 1. 先查调用询角色 2. 在调用修改角色
    AdminRole selectRoleById(@Param("id") Long id);
    Integer updateRoleById(@Param("id") Long id, @Param("dto") AdminRoleUpdateDTO dto, @Param("adminId") Long adminId);

    // 改变角色状态接口
    Integer updateRoleStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("adminId") Long adminId);

    // 逻辑删除角色接口 - 1. 先查角色用户接口 2. 在调用逻辑删除角色接口
    Integer countUserRoleByRoleId(@Param("roleId") Long roleId);
    Integer logicalDeleteRole(@Param("id") Long id, @Param("adminId") Long adminId);


    Integer countNormalRoleById(@Param("roleId") Long roleId);
}
