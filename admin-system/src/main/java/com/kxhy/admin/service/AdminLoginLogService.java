package com.kxhy.admin.service;


import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminLoginLogQueryDTO;
import com.kxhy.admin.domain.vo.AdminLoginLogVO;

public interface AdminLoginLogService {

    PageInfo<AdminLoginLogVO> queryLoginLogPage(AdminLoginLogQueryDTO query);

}
