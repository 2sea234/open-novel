package com.kxhy.novel.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class NetworkUtil {


    /**
     * 获取当前请求
     * @return 当前请求
     */
    public HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取第一个IP
     * @param ip  IP
     * @return 第一个IP
     */
    private String getFirstIP(String ip) {
        if (ip.contains(",")) {
            return ip.split(",")[0].trim();
        }
        return ip;
    }


    /**
     * IP是否有效
     * @param ip  IP
     * @return 是否有效
     */
    private boolean isValidIP(String ip) {
        return ip != null && ip.length() > 0 && !"unkown".equalsIgnoreCase(ip) && !"0:0:0:0:0:0:0:1".equals(ip);
    }

    /**
     * 获取客户端IP
     * @param request  请求
     * @return  IP
     */
    public String getClientIP(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (isValidIP(ip)) {
                return getFirstIP(ip);
            }
        }
        return request.getRemoteAddr();
    }


}
