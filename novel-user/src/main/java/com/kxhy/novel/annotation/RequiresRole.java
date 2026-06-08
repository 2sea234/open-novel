package com.kxhy.novel.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRole {

    // 需要的角色
    String value(); // reader | author | admin

    String message() default "权限不足";

}
