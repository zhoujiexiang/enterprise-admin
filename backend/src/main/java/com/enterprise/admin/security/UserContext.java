package com.enterprise.admin.security;

/**
 * 当前登录用户上下文（线程隔离）
 * 拦截器解析 JWT 后写入，Controller/Service 中读取
 */
public class UserContext {

    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();

    public static void setUserId(Long id) {
        userId.set(id);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static void setUsername(String name) {
        username.set(name);
    }

    public static String getUsername() {
        return username.get();
    }

    public static void clear() {
        userId.remove();
        username.remove();
    }
}
