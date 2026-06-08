package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.AdminLoginLogQueryDTO;
import com.kxhy.admin.domain.po.AdminLoginLog;
import com.kxhy.admin.domain.vo.AdminLoginLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminLoginLogMapper {

    int insertLoginLog(AdminLoginLog loginLog);

    List<AdminLoginLogVO> queryLoginLogById(@Param("query") AdminLoginLogQueryDTO query);

}
