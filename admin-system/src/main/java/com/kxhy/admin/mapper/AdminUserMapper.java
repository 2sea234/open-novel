package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.user.AdminUserUpdateDTO;
import com.kxhy.admin.domain.po.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface AdminUserMapper {


    AdminUser listAdminUser(@Param("id") Long id);

    Integer countByUsername(String username);

    Integer insertAdminUser(AdminUser adminUser);

    Integer updateAdminUser(@Param("id") Long id, @Param("dto") AdminUserUpdateDTO dto, @Param("adminId") Long adminId);

    Integer updateAdminUserStatus(@Param("id") Long id, @Param("status") Integer status, @Param("adminId") Long adminId);

    Integer resetAdminUserPassword(@Param("id") Long id, @Param("password") String  password, @Param("adminId") Long adminId);

    Integer deleteAdminUser(@Param("id") Long id, @Param("adminId") Long adminId);

    /**
     * 用户存在校验
     * @return Integer
     */
    Integer countNormalUserById(@Param("userId") Long id);

    AdminUser selectUserById(@Param("id") Long id);

}
