package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.AdminLoginDTO;
import com.kxhy.admin.domain.po.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminLoginMapper {


    @Select("select id, username, password, nickname, status, last_login_time lastLoginTime, last_login_ip lastLoginIp, create_time createTime, update_time updateTime, is_deleted isDeleted from admin_user where username = #{adminLoginDTO.username} and is_deleted = 0")
    AdminUser queryAdminUser(@Param("adminLoginDTO") AdminLoginDTO adminLoginDTO);




}
