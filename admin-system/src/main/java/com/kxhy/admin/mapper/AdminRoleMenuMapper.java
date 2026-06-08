package com.kxhy.admin.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联表
 */
@Mapper
public interface AdminRoleMenuMapper {

    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    void deleteMenusByRoleId(@Param("roleId") Long roleId);

    int batchInsertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds, @Param("adminId") Long adminId);

}
