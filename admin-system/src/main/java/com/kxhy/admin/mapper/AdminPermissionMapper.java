package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.vo.AdminPermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminPermissionMapper {

    List<String> selectPermissionCodesByAdminId(@Param("adminId") Long adminId);

    Integer countNormalPermissionsByIds(@Param("permissionIds") List<Long> permissionIds);

    List<AdminPermissionVO> selectPermissionList();

}
