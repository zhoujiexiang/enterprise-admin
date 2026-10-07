package com.enterprise.admin.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprise.admin.common.BusinessException;
import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.entity.SysUser;
import com.enterprise.admin.entity.SysUserRole;
import com.enterprise.admin.mapper.SysUserMapper;
import com.enterprise.admin.mapper.SysUserRoleMapper;
import com.enterprise.admin.security.UserContext;
import com.enterprise.admin.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public PageResult<SysUser> page(int page, int size, String username, String phone, String status) {
        Page<SysUser> p = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(SysUser::getPhone, phone);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getUserId);
        Page<SysUser> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public SysUser getUserById(Long userId) {
        SysUser user = getById(userId);
        if (user != null) {
            // 查询角色ID列表
            List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                    new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
            List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            user.setRoleIds(roleIds);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(SysUser user) {
        // 用户名查重（仅校验未删除的活跃记录）
        if (checkUsernameExists(user.getUsername(), null)) {
            throw new BusinessException("用户名已存在");
        }
        // 密码 BCrypt 加密
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setCreateBy(UserContext.getUsername());
        user.setCreateTime(LocalDateTime.now());
        boolean ok = save(user);
        // 保存用户角色关联
        saveUserRoles(user.getUserId(), user.getRoleIds());
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUser user) {
        // 用户名查重（排除自身）
        if (checkUsernameExists(user.getUsername(), user.getUserId())) {
            throw new BusinessException("用户名已存在");
        }
        user.setUpdateBy(UserContext.getUsername());
        user.setUpdateTime(LocalDateTime.now());
        // 不更新密码字段（设为 null，MyBatis-Plus 默认只更新非 null 字段）
        user.setPassword(null);
        boolean ok = updateById(user);
        // 先删旧角色关联再插新的
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getUserId()));
        saveUserRoles(user.getUserId(), user.getRoleIds());
        return ok;
    }

    /** 批量保存用户角色关联 */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
    }

    /** 校验用户名是否已被未删除的记录占用（excludeId 为排除自身的 userId） */
    private boolean checkUsernameExists(String username, Long excludeId) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(SysUser::getUserId, excludeId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public boolean removeUserByIds(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public boolean resetPwd(Long userId, String oldPassword, String newPassword) {
        SysUser dbUser = getById(userId);
        if (dbUser == null) {
            throw new BusinessException("用户不存在");
        }
        // 校验原密码
        if (!BCrypt.checkpw(oldPassword, dbUser.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setUpdateBy(UserContext.getUsername());
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }

    @Override
    public boolean changeStatus(Long userId, String status) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        user.setUpdateBy(UserContext.getUsername());
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }
}
