package com.kxhy.novel.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.kxhy.novel.annotation.RequiresRole;
import com.kxhy.novel.constant.NovelPermissionConstants;
import com.kxhy.novel.exception.BusinessException;
import com.kxhy.novel.mapper.NovelRoleMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NovelPermissionAspect {

    private final NovelRoleMapper novelRoleMapper;

    /**
     * 阅读权限校验
     */
    @Before("@annotation(requiresRole)")
    public void checkReadPermission(JoinPoint joinPoint, RequiresRole requiresRole) {
        if (!StpUtil.isLogin()) {
            throw new BusinessException(401, "请先登录");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresRole annotation = method.getAnnotation(RequiresRole.class);
        String requiredRole = annotation.value();
        long userId = Long.parseLong(StpUtil.getLoginId().toString());

        // 获取用户角色
        List<String> userRoles = novelRoleMapper.selectUserRoles(userId);

        boolean hasPermission = userRoles.stream()
                .anyMatch(userRole -> NovelPermissionConstants.hasHigherOrEqualRole(userRole, requiredRole));


        if (!hasPermission) {
            log.warn("阅读权限校验失败 - 用户ID: {}, 拥有角色: {}, 需要角色{}", userId, userRoles, requiredRole);
            throw new BusinessException(403, annotation.message());
        }

        log.debug("阅读权限校验通过 - 用户ID: {}, 需要角色: {}", userId, requiresRole);
    }

}