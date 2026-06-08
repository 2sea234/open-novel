package com.kxhy.gateway.filter;


import com.opennovel.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private static final String LOGIN_PATH = "/api/adminSystem/adminLogin";
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // 预检查请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = request.getURI().getPath();
        if (LOGIN_PATH.equals(path) || !path.startsWith("/api/adminSystem/")) {
            return chain.filter(exchange);
        }

        // 读取 Authorization请求头
        String authorization = request.getHeaders().getFirst("Authorization");

        if (authorization == null || authorization.isBlank()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (!authorization.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authorization.substring(7);


        // 解析token
        try {
            Claims claims = jwtUtil.parseToken(token);
            // 先校验 loginType
            Object loginTypeObj = claims.get("loginType");
            String loginType = loginTypeObj == null ? null : loginTypeObj.toString();
            if (!"ADMIN".equals(loginType)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 管理员信息
            String adminId = claims.get("adminId").toString();
            String username = claims.get("username").toString();

            ServerHttpRequest newRequest = request.mutate()
                    .headers(headers -> {
                        headers.remove("X-Admin-Id");
                        headers.remove("X-Admin-Username");
                    })
                    .header("X-Admin-Id", adminId)
                    .header("X-Admin-Username", username)
                    .build();

            ServerWebExchange newExchange = exchange.mutate()
                    .request(newRequest)
                    .build();
            return chain.filter(newExchange);
        }catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
