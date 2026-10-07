package com.enterprise.admin.security;

import com.enterprise.admin.common.BusinessException;
import com.enterprise.admin.service.SysMenuService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 权限校验切面
 * 拦截 @PreAuthorize 注解，校验当前用户是否拥有对应权限
 */
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private SysMenuService menuService;

    @Around("@annotation(preAuthorize)")
    public Object check(ProceedingJoinPoint joinPoint, PreAuthorize preAuthorize) throws Throwable {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 超级管理员直接放行
        if (userId == 1L) {
            return joinPoint.proceed();
        }

        // 获取当前用户拥有的权限标识集合
        Set<String> userPerms = menuService.getUserPerms(userId);

        String requiredPerms = preAuthorize.value();
        PreAuthorize.Logical logical = preAuthorize.logical();

        boolean hasPermission;
        if (logical == PreAuthorize.Logical.AND) {
            hasPermission = userPerms.contains(requiredPerms);
        } else {
            hasPermission = userPerms.contains(requiredPerms);
        }

        if (!hasPermission) {
            throw new BusinessException(403, "没有操作权限：" + requiredPerms);
        }

        return joinPoint.proceed();
    }
}
