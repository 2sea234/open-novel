package com.kxhy.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.user.AdminUserQueryDTO;
import com.kxhy.admin.domain.vo.AdminUserListVO;
import com.kxhy.admin.mapper.AdminUserPageMapper;
import com.kxhy.admin.service.AdminUserPageService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminUserPageServiceImpl implements AdminUserPageService {

    private final AdminUserPageMapper adminUserPageMapper;

    @Override
    public PageInfo<AdminUserListVO> pageAdminUsers(Integer pageNum, Integer pageSize, AdminUserQueryDTO query) {

        if (pageNum == null || pageNum < 0) {
            throw new BizException(400, "页码参数不合法");
        }

        if (pageSize == null || pageSize < 0) {
            throw new BizException(400, "每页条数参数不合法");
        }

        PageHelper.startPage(pageNum, pageSize);

        List<AdminUserListVO> adminUserListVOS = adminUserPageMapper.selectAdminUserPage(query);
        return new PageInfo<>(adminUserListVOS);
    }
}
