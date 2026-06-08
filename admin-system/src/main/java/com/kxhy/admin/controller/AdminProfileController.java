package com.kxhy.admin.controller;


import com.kxhy.admin.domain.dto.AdminProfilePasswordDTO;
import com.kxhy.admin.domain.dto.AdminProfileUpdateDTO;
import com.kxhy.admin.domain.vo.AdminProfileVO;
import com.kxhy.admin.service.AdminProfileService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("adminSystem/profile")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;
//    @RequirePermission("admin:profile:view")
    @GetMapping
    public Result<AdminProfileVO> queryProfile(@RequestHeader(value = "X-Admin-Id", required = true) Long adminId) {
        return Result.success(adminProfileService.selectProfileById(adminId));
    }

//    @RequirePermission("admin:profile:update")
    @PutMapping
    public Result<Void> updateProfile(@RequestBody AdminProfileUpdateDTO dto,
                                      @RequestHeader(value = "X-Admin-Id", required = true) Long adminId,
                                      @RequestHeader(value = "X-Admin-Username", required = true) String username) {
        adminProfileService.updateProfile(adminId, dto, username);
        return Result.success();
    }

//    @RequirePermission("admin:profile:password")
    @PutMapping("password")
    public Result<Void> updatePassword(@RequestBody AdminProfilePasswordDTO dto,
                                       @RequestHeader(value = "X-Admin-Id", required = true) Long adminId,
                                       @RequestHeader(value = "X-Admin-Username", required = true) String username) {
        adminProfileService.updateProfilePassword(adminId, dto, username);
        return Result.success();
    }

}
