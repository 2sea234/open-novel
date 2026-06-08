package com.kxhy.admin.controller;

import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminOperationLogQueryDTO;
import com.kxhy.admin.domain.vo.AdminOperationLogVO;
import com.kxhy.admin.service.AdminOperationLogService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 操作日志控制器
 * @Author: kxhy
 * @Date: 2026/6/2
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("adminSystem/operation")
public class AdminOperationLogController {

    private final AdminOperationLogService adminOperationLogService;

    @RequirePermission("admin:operation-log:list")
    @GetMapping("page")
    public Result<PageInfo<AdminOperationLogVO>> queryOperationLogPage(AdminOperationLogQueryDTO query) {
        PageInfo<AdminOperationLogVO> pageInfo = adminOperationLogService.queryOperationLogList(query);
        return Result.success(pageInfo);
    }

}
