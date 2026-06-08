package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.AdminOperationLogQueryDTO;
import com.kxhy.admin.domain.po.AdminOperationLog;
import com.kxhy.admin.domain.vo.AdminOperationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminOperationLogMapper {

    int insertOperationLog(AdminOperationLog log);

    List<AdminOperationLogVO> selectOperationLogPage(@Param("query") AdminOperationLogQueryDTO query);

}
