package com.kxhy.admin.controller;

import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminLoginLogQueryDTO;
import com.kxhy.admin.domain.vo.AdminLoginLogVO;
import com.kxhy.admin.service.AdminLoginLogService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 管理员登录日志
 */
@RestController
@RequestMapping("adminSystem/log")
@RequiredArgsConstructor
public class AdminLogController {

    private final AdminLoginLogService adminLoginLogService;

    @RequirePermission("admin:login-log:list")
    @GetMapping("page")
    public Result<PageInfo<AdminLoginLogVO>> queryLogPage(AdminLoginLogQueryDTO query) {
        PageInfo<AdminLoginLogVO> adminLoginLogVOPageInfo = adminLoginLogService.queryLoginLogPage(query);
        return Result.success(adminLoginLogVOPageInfo);
    }

}
