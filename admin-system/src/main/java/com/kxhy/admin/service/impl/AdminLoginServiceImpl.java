package com.kxhy.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminLoginDTO;
import com.kxhy.admin.domain.po.AdminLoginLog;
import com.kxhy.admin.domain.po.AdminUser;
import com.kxhy.admin.domain.vo.AdminLoginLogVO;
import com.kxhy.admin.domain.vo.AdminLoginVo;
import com.kxhy.admin.mapper.AdminLoginLogMapper;
import com.kxhy.admin.mapper.AdminLoginMapper;
import com.kxhy.admin.service.AdminLoginService;
import com.opennovel.common.exception.BizException;
import com.opennovel.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
@RequiredArgsConstructor
    public class AdminLoginServiceImpl implements AdminLoginService {

    private final AdminLoginMapper adminLoginMapper;
    private final JwtUtil jwtUtil;
    private final AdminLoginLogMapper adminLoginLogMapper;


    @Override
    public AdminLoginVo login(AdminLoginDTO adminLoginDTO) {
        AdminUser adminUser = adminLoginMapper.queryAdminUser(adminLoginDTO);
        if (adminUser != null) {

            if (adminUser.getStatus() != 1) {
                recordLoginLog(adminLoginDTO.getUsername(), 0, "账号已被禁用");
                throw new BizException(403, "账号已被禁用");
            }


            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (!encoder.matches(adminLoginDTO.getPassword(), adminUser.getPassword())) {
                recordLoginLog(adminLoginDTO.getUsername(), 0, "账号或密码错误");
                throw new BizException(401, "账号或密码错误");
            }
            recordLoginLog(adminLoginDTO.getUsername(), 1, "");
            return new AdminLoginVo(
                    jwtUtil.createJwt(adminUser.getId(), adminUser.getUsername()),
                    "Bearer",
                    adminUser.getId(),
                    adminUser.getUsername(),
                    adminUser.getNickname()
            );
        }
        recordLoginLog(adminLoginDTO.getUsername(), 0, "账号已被删除或不存在");
        throw new BizException(403, "账号已被删除或不存在");
    }

    private void recordLoginLog(String username, Integer status, String errorMessage) {
        AdminLoginLog loginLog = new AdminLoginLog();
        loginLog.setUsername(username);
        loginLog.setLoginStatus(status);
        loginLog.setLoginIp(getClientIp());
        loginLog.setUserAgent(getUserAgent());
        loginLog.setErrorMessage(errorMessage);

        adminLoginLogMapper.insertLoginLog(loginLog);
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();

    }

    private String getUserAgent() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest().getHeader("User-Agent");
    }
}
