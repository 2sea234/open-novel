package com.kxhy.admin.service;


import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminLoginDTO;
import com.kxhy.admin.domain.vo.AdminLoginLogVO;
import com.kxhy.admin.domain.vo.AdminLoginVo;

public interface AdminLoginService {

    AdminLoginVo login(AdminLoginDTO adminLoginDTO);



}
