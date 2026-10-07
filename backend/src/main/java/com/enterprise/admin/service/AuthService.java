package com.enterprise.admin.service;

import com.enterprise.admin.common.Result;
import com.enterprise.admin.dto.LoginDTO;
import com.enterprise.admin.dto.RouterVO;

import java.util.List;
import java.util.Map;

/**
 * 认证服务
 */
public interface AuthService {

    /** 生成图形验证码 */
    Result<Map<String, Object>> captcha();

    /** 登录 */
    Result<Map<String, Object>> login(LoginDTO dto);

    /** 获取当前登录用户信息（用户、角色、权限） */
    Result<Map<String, Object>> getInfo();

    /** 获取当前用户的路由树 */
    Result<List<RouterVO>> getRouters();

    /** 退出登录 */
    Result<Void> logout();
}
