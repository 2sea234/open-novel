package com.kxhy.admin.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminRolePermissionMapper {

    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    void deletePermissionsByRoleId(@Param("roleId") Long roleId);

    int batchInsertRolePermissions(@Param("roleId") Long roleId,
                                  @Param("permissionIds") List<Long> permissionIds,
                                  @Param("adminId") Long adminId);

}
