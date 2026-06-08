package com.kxhy.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminLoginLogQueryDTO;
import com.kxhy.admin.domain.vo.AdminLoginLogVO;
import com.kxhy.admin.mapper.AdminLoginLogMapper;
import com.kxhy.admin.service.AdminLoginLogService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminLoginLogServiceImpl implements AdminLoginLogService {

    private final AdminLoginLogMapper adminLoginLogMapper;

    @Override
    public PageInfo<AdminLoginLogVO> queryLoginLogPage(AdminLoginLogQueryDTO query) {

        if (query == null) {
            query = new AdminLoginLogQueryDTO();
        }

        if (query.getPageNum() == null || query.getPageNum() <= 0) {
            query.setPageNum(1);
        }

        if (query.getPageSize() == null || query.getPageSize() <= 0) {
            query.setPageSize(10);
        }



        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        return new PageInfo<>(adminLoginLogMapper.queryLoginLogById(query));
    }
}
