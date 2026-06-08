package com.kxhy.admin.controller;


import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.notice.AdminNoticeAddDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeQueryDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeStatusDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeUpdateDTO;
import com.kxhy.admin.domain.vo.AdminNoticeVO;
import com.kxhy.admin.service.AdminNoticeService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公告管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("adminSystem/notice")
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    @RequirePermission("admin:notice:list")
    @GetMapping("page")
    public Result<PageInfo<AdminNoticeVO>> queryNoticePage(AdminNoticeQueryDTO query) {
        return Result.success(adminNoticeService.queryNoticePage(query));
    }

    @RequirePermission("admin:notice:add")
    @PostMapping
    public Result<AdminNoticeAddDTO> addNotice(@RequestBody AdminNoticeAddDTO dto,
                                               @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                               @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        adminNoticeService.addNotice(dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:notice:update")
    @PutMapping("{id}")
    public Result<AdminNoticeUpdateDTO> modifyNotice(@PathVariable Long id,
                                                     @RequestBody AdminNoticeUpdateDTO dto,
                                                     @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                                     @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        adminNoticeService.updateNoticeById(id, dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:notice:publish")
    @PutMapping("{id}/status")
    public Result<Void> modifyNoticeStatus(@PathVariable Long id,
                                             @RequestBody AdminNoticeStatusDTO dto,
                                             @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                             @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        adminNoticeService.modifyStatusById(id, dto, adminId, username);
        return Result.success();
    }

    @RequirePermission("admin:notice:delete")
    @DeleteMapping("{id}")
    public Result<Void> deleteNotice(@PathVariable Long id,
                                     @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
                                     @RequestHeader(value = "X-Admin-Username", required = false) String username) {
        adminNoticeService.deleteNoticeById(id, adminId, username);
        return Result.success();
    }

}
