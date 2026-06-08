package com.kxhy.novel.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // 优先级最高,最先执行
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String RESPONSE_REQUEST_ID_HEADER = "X-Request-ID";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // 获取请求ID
        String requestId = getOrGenerateRequestId(httpRequest);

        // 设置到响应头中
        httpResponse.setHeader(RESPONSE_REQUEST_ID_HEADER, requestId);

        // 设置到MDC中，便于日志跟踪
        MDC.put("requestId", requestId);

        long startTime = System.currentTimeMillis();

        try {
            log.debug("请求开始 - 方法：{}，路径：{}，请求ID：{}", httpRequest.getMethod(), httpRequest.getRequestURI(), requestId);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.debug("请求结束 - 路径：{}，请求ID：{}，耗时：{}ms", httpRequest.getRequestURI(), requestId, duration);
            MDC.remove("requestId");
        }


    }


    /**
     * 生成请求ID
     * @return ID
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 获取请求ID
     * @param request 请求
     * @return  ID
     */
    private String getOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = generateRequestId();
        }
        return requestId;
    }
}
