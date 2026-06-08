package com.kxhy.novel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NovelRoleMapper {

    @Select("SELECT id FROM novel_role WHERE code = #{roleCode}")
    Long selectRoleIdByCode(@Param("roleCode") String roleCode);

    @Insert("INSERT INTO novel_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int assignRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select("SELECT r.code FROM novel_role r " +
            "INNER JOIN novel_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectUserRoles(@Param("userId") Long userId);

}
