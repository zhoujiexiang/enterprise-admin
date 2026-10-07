package com.enterprise.admin.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 用在 Controller 方法上，标注所需权限标识
 * 示例：@PreAuthorize("system:user:list")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreAuthorize {
    /**
     * 权限标识，对应 sys_menu.perms 字段
     */
    String value();

    /**
     * 逻辑：AND（需要全部权限） / OR（任一权限即可）
     */
    Logical logical() default Logical.AND;

    enum Logical {
        AND, OR
    }
}
