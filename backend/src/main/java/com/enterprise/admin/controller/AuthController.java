package com.enterprise.admin.controller;

import com.enterprise.admin.common.Result;
import com.enterprise.admin.dto.LoginDTO;
import com.enterprise.admin.dto.RouterVO;
import com.enterprise.admin.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** 获取图形验证码 */
    @GetMapping("/captcha")
    public Result<Map<String, Object>> captcha() {
        return authService.captcha();
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginDTO dto) {
        return authService.login(dto);
    }

    /** 获取当前登录用户信息（用户、角色、权限） */
    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo() {
        return authService.getInfo();
    }

    /** 获取当前用户的路由树 */
    @GetMapping("/getRouters")
    public Result<List<RouterVO>> getRouters() {
        return authService.getRouters();
    }

    /** 退出登录 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return authService.logout();
    }
}
