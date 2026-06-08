package com.kxhy.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminUserRoleMapper {

    Integer deleteRolesByUserId(@Param("userId") Long userId);

    Integer batchInsertRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds, @Param("adminId") Long adminId);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

}
