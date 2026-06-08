package com.opennovel.common.annotation;


import java.lang.annotation.*;

/**
 * 接口权限校验注解
 * 用在 Controller 方法上，表示访问该接口需要具备指定权限码。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限码
     * @return 例如：admin:user:delete
     */
    String value();

}
