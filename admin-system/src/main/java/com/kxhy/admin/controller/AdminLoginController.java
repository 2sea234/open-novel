package com.kxhy.admin.controller;

import com.kxhy.admin.domain.dto.AdminLoginDTO;
import com.kxhy.admin.domain.vo.AdminLoginVo;
import com.kxhy.admin.service.AdminLoginService;
import com.opennovel.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 管理员登录接口
 */
@RestController
@RequestMapping("adminSystem")
@RequiredArgsConstructor
public class AdminLoginController {
    private final AdminLoginService adminLoginService;


    @PostMapping("adminLogin")
    public Result<AdminLoginVo> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        AdminLoginVo login = adminLoginService.login(adminLoginDTO);
        return Result.success(login);
    }



}
