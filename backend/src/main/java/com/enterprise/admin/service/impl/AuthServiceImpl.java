package com.enterprise.admin.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.admin.common.BusinessException;
import com.enterprise.admin.common.Result;
import com.enterprise.admin.dto.LoginDTO;
import com.enterprise.admin.dto.RouterVO;
import com.enterprise.admin.entity.SysMenu;
import com.enterprise.admin.entity.SysRole;
import com.enterprise.admin.entity.SysUser;
import com.enterprise.admin.entity.SysUserRole;
import com.enterprise.admin.mapper.SysMenuMapper;
import com.enterprise.admin.mapper.SysRoleMapper;
import com.enterprise.admin.mapper.SysUserMapper;
import com.enterprise.admin.mapper.SysUserRoleMapper;
import com.enterprise.admin.security.JwtUtils;
import com.enterprise.admin.security.UserContext;
import com.enterprise.admin.service.AuthService;
import com.enterprise.admin.service.SysMenuService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private JwtUtils jwtUtils;

    // 验证码内存缓存：captchaKey -> captcha 文本
    private final Map<String, String> captchaCache = new ConcurrentHashMap<>();

    @Override
    public Result<Map<String, Object>> captcha() {
        // 生成验证码文本
        String captchaText = defaultKaptcha.createText();
        // 生成验证码图片
        BufferedImage image = defaultKaptcha.createImage(captchaText);
        // 用 UUID 作为 key 存入缓存
        String captchaKey = UUID.randomUUID().toString();
        captchaCache.put(captchaKey, captchaText);
        // Base64 编码图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (Exception e) {
            throw new BusinessException("验证码生成失败");
        }
        String img = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        Map<String, Object> data = new HashMap<>();
        data.put("captchaKey", captchaKey);
        data.put("img", img);
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> login(LoginDTO dto) {
        // 校验验证码
        String storedCaptcha = captchaCache.get(dto.getCaptchaKey());
        if (storedCaptcha == null) {
            return Result.error("验证码已过期，请刷新");
        }
        if (!storedCaptcha.equalsIgnoreCase(dto.getCaptcha())) {
            captchaCache.remove(dto.getCaptchaKey());
            return Result.error("验证码错误");
        }
        captchaCache.remove(dto.getCaptchaKey());
        // 查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if ("1".equals(user.getStatus())) {
            return Result.error("账号已被停用，请联系管理员");
        }
        // BCrypt 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        // 生成 JWT
        String token = jwtUtils.generateToken(user.getUserId(), user.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> getInfo() {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 查询角色 roleKey 列表
        List<String> roles;
        if (userId != null && userId == 1L) {
            // admin 查所有正常角色
            List<SysRole> allRoles = sysRoleMapper.selectList(
                    new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, "0"));
            roles = allRoles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        } else {
            roles = getUserRoleKeys(userId);
        }
        // 查询权限标识集合
        Set<String> permissions;
        if (userId != null && userId == 1L) {
            // admin 查所有按钮权限
            List<SysMenu> allMenus = sysMenuMapper.selectList(
                    new LambdaQueryWrapper<SysMenu>()
                            .ne(SysMenu::getPerms, "")
                            .eq(SysMenu::getMenuType, "F"));
            permissions = allMenus.stream().map(SysMenu::getPerms).collect(Collectors.toSet());
        } else {
            permissions = sysMenuMapper.selectPermsByUserId(userId);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roles", roles);
        data.put("permissions", permissions);
        return Result.success(data);
    }

    /** 查询用户的角色 roleKey 列表 */
    private List<String> getUserRoleKeys(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getRoleId, roleIds)
                        .eq(SysRole::getStatus, "0"));
        return roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
    }

    @Override
    public Result<List<RouterVO>> getRouters() {
        Long userId = UserContext.getUserId();
        List<RouterVO> routers = sysMenuService.buildMenus(userId);
        return Result.success(routers);
    }

    @Override
    public Result<Void> logout() {
        // JWT 无状态，前端删 token 即可，此处不做额外处理
        return Result.success();
    }
}
