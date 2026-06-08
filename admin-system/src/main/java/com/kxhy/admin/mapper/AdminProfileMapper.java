package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.AdminProfileUpdateDTO;
import com.kxhy.admin.domain.vo.AdminProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminProfileMapper {

    AdminProfileVO selectProfileById(@Param("adminId") Long adminId);

    String selectPasswordById(@Param("adminId") Long adminId);

    int updateProfile(@Param("adminId") Long adminId, @Param("dto") AdminProfileUpdateDTO dto);

    int updateProfilePassword(@Param("adminId") Long adminId, @Param("encodedPassword") String encodedPassword);

}
