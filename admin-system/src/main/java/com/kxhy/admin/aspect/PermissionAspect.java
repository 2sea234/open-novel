package com.kxhy.admin.aspect;

import com.kxhy.admin.service.AdminPermissionService;
import com.opennovel.common.annotation.RequirePermission;
import com.opennovel.common.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final AdminPermissionService adminPermissionService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint,
                                  RequirePermission requirePermission) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BizException(401, "当前管理员未登录");
        }

        HttpServletRequest request = attributes.getRequest();

        String adminIdHeader = request.getHeader("X-Admin-Id");

        if (adminIdHeader == null || adminIdHeader.isBlank()) {
            throw new BizException(401, "当前管理员未登录");
        }

        Long adminId;

        try {
            adminId = Long.valueOf(adminIdHeader);
        } catch (NumberFormatException e) {
            throw new BizException(401, "当前管理员身份不合法");
        }

        String requiredPermission = requirePermission.value();

        List<String> permissionCodeList = adminPermissionService.queryPermissionCodesByAdminId(adminId);

        Set<String> permissionCodeSet = new HashSet<>(permissionCodeList);

        if (!permissionCodeSet.contains(requiredPermission)) {
            log.warn("权限校验失败：adminId={}, requiredPermission={}", adminId, requiredPermission);
            throw new BizException(403, "无权限访问");
        }

        return joinPoint.proceed();
    }

}
