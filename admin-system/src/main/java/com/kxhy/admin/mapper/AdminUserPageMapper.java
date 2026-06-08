package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.user.AdminUserQueryDTO;
import com.kxhy.admin.domain.vo.AdminUserListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminUserPageMapper {

    List<AdminUserListVO> selectAdminUserPage(@Param("query") AdminUserQueryDTO query);

}
