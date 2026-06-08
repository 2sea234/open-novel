package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.notice.AdminNoticeAddDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeQueryDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeUpdateDTO;
import com.kxhy.admin.domain.vo.AdminNoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminNoticeMapper {

    List<AdminNoticeVO> selectNoticePage(@Param("query") AdminNoticeQueryDTO query);


    int insertNotice(@Param("dto")AdminNoticeAddDTO dto, @Param("adminId") Long adminId);

    Integer countNormalNoticeById(Long id);
    int updateNoticeById(@Param("id") Long id, @Param("dto") AdminNoticeUpdateDTO dto, @Param("adminId") Long adminId);

    int updateNoticeStatus(@Param("id") Long id, @Param("status") Integer status, @Param("adminId") Long adminId);

    int deleteNoticeById(@Param("id") Long id, @Param("adminId") Long adminId);

}
