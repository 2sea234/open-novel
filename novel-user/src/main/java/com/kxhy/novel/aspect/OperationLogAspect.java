package com.kxhy.novel.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxhy.novel.annotation.OperationLog;
import com.kxhy.novel.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;


    /**
     * 获取请求对象
     * @return 请求对象
     */
    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取客户端IP
     * @param request 请求对象
     * @return 客户端IP
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取IP地址位置
     * @param ip IP地址
     * @return IP地址位置
     */
    private String getIpLocation(String ip) {
        if (ip.startsWith("192.169.") || ip.startsWith("10.") || ip.startsWith("172.")) {
            return "内网IP";
        }
        return "未知";
    }

    /**
     * 设置用户信息
     * @param operationLog 操作日志
     */
    private void setUserInfo(com.kxhy.novel.domain.po.OperationLog operationLog) {
        try {
            // 从Sa-Token中获取信息
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                operationLog.setUserId(Long.parseLong(loginId.toString()));
                operationLog.setUsername(loginId.toString());
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败", e);
        }
    }

    /**
     * 保存操作日志
     * @param joinPoint 切点
     * @param operationLog 操作日志注解
     * @param result 结果
     * @param costTime 执行时间
     * @param isSuccess 是否成功
     * @param errorMsg 错误信息
     */
    private void saveOperationLog(ProceedingJoinPoint joinPoint, OperationLog operationLog,
                                  Object result, long costTime, boolean isSuccess, String errorMsg) {
        HttpServletRequest request = getRequest();
        if (result == null) {
            log.warn("无法获取HttpServletRequest，跳过操作日志记录");
            return;
        }

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 创建操作日志对象
        com.kxhy.novel.domain.po.OperationLog log = new com.kxhy.novel.domain.po.OperationLog();
        log.setModule(operationLog.module());
        log.setType(operationLog.type());
        log.setDescription(operationLog.description());
        log.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
        log.setRequestMethod(request.getMethod());
        log.setRequestUrl(request.getRequestURI());
        log.setIp(getClientIP(request));
        log.setLocation(getIpLocation(log.getIp()));

        // 记录请求参数
        if (operationLog.recordParams()) {
            try {
                String params = objectMapper.writeValueAsString(joinPoint.getArgs());
                log.setParams(params.length() > 2000 ? params.substring(0, 2000) : params);
            } catch (Exception e) {
                log.setParams("参数序列化失败");
            }
        }

        // 记录返回结果
        if (operationLog.recordResult() && result != null) {
            try {
                String resultStr = objectMapper.writeValueAsString(result);
                log.setResult(resultStr.length() > 2000 ? resultStr.substring(0, 2000) : resultStr);
            } catch (Exception e) {
                log.setResult("结果序列化失败");
            }
        }

        // 记录执行时间
        if (operationLog.recordTime()) {
            log.setCostTime(costTime);
        }

        // 设置用户信息
        setUserInfo(log);

        log.setStatus(isSuccess ? 1 : 0);
        log.setErrorMsg(errorMsg);
        log.setCreateTime(LocalDateTime.now());

        // 异步保存到数据库
        operationLogService.saveAsync(log);

    }

    /**
     * 环绕通知
     * @param joinPoint 切点
     * @param operationLog 操作日志注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = null;
        boolean isSuccess = true;
        String errorMsg = null;
        try {
            // 执行原方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            isSuccess = false;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            try {
                long cosTime = System.currentTimeMillis() - startTime;
                // 记录操作日志
                saveOperationLog(joinPoint, operationLog, result, cosTime, isSuccess, errorMsg);
            } catch (Exception e) {
                log.error("记录操作日志失败", e);
            }
        }
    }

}
