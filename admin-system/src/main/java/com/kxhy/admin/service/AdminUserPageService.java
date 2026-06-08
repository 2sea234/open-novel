package com.kxhy.admin.service;


import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.user.AdminUserQueryDTO;
import com.kxhy.admin.domain.vo.AdminUserListVO;

public interface AdminUserPageService {

    PageInfo<AdminUserListVO> pageAdminUsers(Integer pageNum, Integer pageSize, AdminUserQueryDTO query);

}
