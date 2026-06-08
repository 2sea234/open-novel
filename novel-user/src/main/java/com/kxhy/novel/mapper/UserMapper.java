package com.kxhy.novel.mapper;

import com.kxhy.novel.domain.po.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {


    /**
     * 注册用户
     * @param user User
     * @return User
     */
    @Insert("insert into novel_user(user_id, username, password, phone, email, status, last_login_time, create_time) values (#{userId}, #{username}, #{password}, #{phone}, #{email}, #{status}, #{lastLoginTime}, #{createTime})")
    Integer insetUser(User user);

    /**
     * 修改用户
     * @param entity user
     * @return User
     */
    @Update("update novel_user set username = #{newName} where user_id = #{userId}")
    int updateUserSelective(Long userId, String newName);

    /**
     * 查询用户
     * @param username 用户名
     * @return User
     */
    @Select("select user_id, username, password, status, last_login_time, create_time from novel_user where username = #{username}")
    User selectByUserName(String username);

    /**
     * 邮箱查重
     * @param email 邮箱
     * @return User
     */
    @Select("select user_id, username, password, status, last_login_time, create_time from novel_user where email = #{email}")
    User selectByEmail(String email);

    /**
     * 根据用户id查询用户
     * @param id 用户id
     * @return  User
     */
    @Select("select user_id, username, password, email, status, last_login_time, create_time from novel_user where user_id = #{user_id}")
    User selectByUserId(String id);

    /**
     * 更新用户退出时间
     * @param lastLoginTime 退出时间
     * @param userId 用户id
     * @return User
     */
    @Update("update novel_user set last_login_time=#{last_login_time} where user_id = #{userId}")
    Integer updateEndTimeById(@Param("last_login_time") LocalDateTime lastLoginTime, @Param("userId") String userId);

    /**
     * 模糊查询用户名  <span style="color: red;">暂时弃用</span>
     * @param pattern 模糊匹配
     * @return User
     */
    @Select("SELECT COUNT(*) FROM user WHERE username LIKE #{pattern}")
    Long countSimilarUsernames(@Param("pattern") String pattern);

}
