package com.kxhy.admin.service.impl;

import com.kxhy.admin.domain.dto.AdminProfilePasswordDTO;
import com.kxhy.admin.domain.dto.AdminProfileUpdateDTO;
import com.kxhy.admin.domain.vo.AdminProfileVO;
import com.kxhy.admin.mapper.AdminProfileMapper;
import com.kxhy.admin.service.AdminOperationLogService;
import com.kxhy.admin.service.AdminProfileService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProfileServiceImpl implements AdminProfileService {

    private final AdminProfileMapper adminProfileMapper;
    private final AdminOperationLogService adminOperationLogService;
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Override
    public AdminProfileVO selectProfileById(Long adminId) {

        if (adminId == null) {
            throw new BizException(401, "当前管理员未登录");
        }
        AdminProfileVO profile = adminProfileMapper.selectProfileById(adminId);
        if (profile == null) {
            throw new BizException(401, "管理员不存在或已删除");
        }
        return profile;
    }

    @Override
    public void updateProfile(Long adminId, AdminProfileUpdateDTO dto, String username) {

        String operationDesc = "修改个人信息，管理员id：" + adminId;

        try {
            if (adminId == null) {
                throw new BizException(401, "当前管理员未登录");
            }

            if (dto == null) {
                throw new BizException(400, "参数不能为空");
            }

            AdminProfileVO oldProfile = adminProfileMapper.selectProfileById(adminId);
            if (oldProfile == null) {
                throw new BizException(404, "管理员不存在或已删除");
            }

            operationDesc = "修改个人信息，管理员id：" + adminId
                    + "，昵称：" + oldProfile.getNickname() + " -> " + dto.getNickname();

            if (oldProfile.getNickname().equals(dto.getNickname())) {
                throw new BizException(400, "昵称不能与当前昵称一致");
            }

            int rows = adminProfileMapper.updateProfile(adminId, dto);
            if (rows <= 0) {
                throw new BizException(500, "更新失败");
            }

            adminOperationLogService.recordSuccess(
                    "用户管理",
                    "修改个人信息",
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "用户管理",
                    "修改个人信息",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }
    }

    @Override
    public void updateProfilePassword(Long adminId, AdminProfilePasswordDTO dto, String username) {

        String operationDesc = "修改个人密码，管理员id：" + adminId;
        try {
            if (adminId == null) {
                throw new BizException(401, "当前管理员未登录");
            }

            if (dto == null) {
                throw new BizException(400, "参数不能为空");
            }

            if (dto.getOldPassword() == null) {
                throw new BizException(400, "旧密码不能为空");
            }

            if (dto.getNewPassword() == null) {
                throw new BizException(400, "新密码不能为空");
            }

            if (dto.getConfirmPassword() == null) {
                throw new BizException(400, "确认密码不能为空");
            }

            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                throw new BizException(400, "两次输入的密码不一致");
            }

            String oldEncodePassword = adminProfileMapper.selectPasswordById(adminId);

            if (oldEncodePassword == null) {
                throw new BizException(404, "管理员不存在或已删除");
            }

            if (!PASSWORD_ENCODER.matches(dto.getOldPassword(), oldEncodePassword)) {
                throw new BizException(400, "旧密码不正确");
            }

            if (PASSWORD_ENCODER.matches(dto.getNewPassword(), oldEncodePassword)) {
                throw new BizException(400, "新密码不能与旧密码一致");
            }

            String encodeNewPassword = PASSWORD_ENCODER.encode(dto.getNewPassword());

            int rows = adminProfileMapper.updateProfilePassword(adminId, encodeNewPassword);

            if (rows <= 0) {
                throw new BizException(500, "修改密码失败");
            }

            adminOperationLogService.recordSuccess(
                    "个人中心",
                    "修改密码",
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {

            adminOperationLogService.recordFail(
                    "个人中心",
                    "修改密码",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );

            throw e;
        }

    }
}
