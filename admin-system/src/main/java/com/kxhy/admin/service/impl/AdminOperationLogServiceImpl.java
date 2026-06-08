package com.kxhy.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminOperationLogQueryDTO;
import com.kxhy.admin.domain.po.AdminOperationLog;
import com.kxhy.admin.domain.vo.AdminOperationLogVO;
import com.kxhy.admin.mapper.AdminOperationLogMapper;
import com.kxhy.admin.mapper.AdminPermissionMapper;
import com.kxhy.admin.service.AdminOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service

@Transactional(propagation = Propagation.REQUIRES_NEW)  // 不管外面的业务事务成功还是失败，操作日志自己单独开一个新事务提交。
@RequiredArgsConstructor
public class AdminOperationLogServiceImpl implements AdminOperationLogService {

    private final AdminOperationLogMapper adminOperationLogMapper;


    @Override
    public void recordSuccess(String operationModule, String operationType, String operationDesc, Long adminId, String username) {

        AdminOperationLog log = buildBaseLog(
                operationModule,
                operationType,
                operationDesc,
                adminId,
                username
        );

        log.setOperationStatus(1);
        log.setErrorMessage(null);
        adminOperationLogMapper.insertOperationLog(log);
    }

    @Override
    public void recordFail(String operationModule, String operationType, String operationDesc, Long adminId, String username, String errorMessage) {
        AdminOperationLog log = buildBaseLog(
                operationModule,
                operationType,
                operationDesc,
                adminId,
                username
        );
        log.setOperationStatus(0);
        log.setErrorMessage(errorMessage);
        adminOperationLogMapper.insertOperationLog(log);

    }

    @Override
    public PageInfo<AdminOperationLogVO> queryOperationLogList(AdminOperationLogQueryDTO query) {

        if (query == null) {
            query = new AdminOperationLogQueryDTO();
        }

        if (query.getPageNum() == null || query.getPageNum() <= 0) {
            query.setPageNum(1);
        }

        if (query.getPageSize() == null || query.getPageSize() <= 0) {
            query.setPageSize(10);
        }

        PageHelper.startPage(query.getPageNum(), query.getPageSize());

        List<AdminOperationLogVO> adminOperationLogVOS = adminOperationLogMapper.selectOperationLogPage(query);
        return new PageInfo<>(adminOperationLogVOS);
    }

    private AdminOperationLog buildBaseLog(String operationModule,
                                           String operationType,
                                           String operationDesc,
                                           Long adminId,
                                           String username) {
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(adminId);
        log.setUsername(username);
        log.setOperationModule(operationModule);
        log.setOperationType(operationType);
        log.setOperationDesc(operationDesc);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (adminId != null) {
            HttpServletRequest request = attributes.getRequest();

            log.setRequestMethod(request.getMethod());
            log.setRequestPath(request.getRequestURI());
            log.setRequestIp(getClientIp(request));
            log.setUserAgent(request.getHeader("User-Agent"));

        }
        return log;
    }

    private String getClientIp(HttpServletRequest request) {

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
}
