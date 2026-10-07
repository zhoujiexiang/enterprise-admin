package com.enterprise.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.entity.SysRole;
import com.enterprise.admin.entity.SysRoleMenu;
import com.enterprise.admin.mapper.SysRoleMapper;
import com.enterprise.admin.mapper.SysRoleMenuMapper;
import com.enterprise.admin.security.UserContext;
import com.enterprise.admin.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public PageResult<SysRole> page(int page, int size, String roleName, String roleKey, String status) {
        Page<SysRole> p = new Page<>(page, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (roleKey != null && !roleKey.isEmpty()) {
            wrapper.like(SysRole::getRoleKey, roleKey);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SysRole::getStatus, status);
        }
        wrapper.orderByDesc(SysRole::getRoleId);
        Page<SysRole> result = page(p, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public SysRole getRoleById(Long roleId) {
        SysRole role = getById(roleId);
        if (role != null) {
            role.setMenuIds(getMenuIds(roleId));
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(SysRole role) {
        role.setCreateBy(UserContext.getUsername());
        role.setCreateTime(LocalDateTime.now());
        boolean ok = save(role);
        // 保存角色菜单关联
        saveRoleMenus(role.getRoleId(), role.getMenuIds());
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        role.setUpdateBy(UserContext.getUsername());
        role.setUpdateTime(LocalDateTime.now());
        boolean ok = updateById(role);
        // 先删旧菜单关联再插新的
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, role.getRoleId()));
        saveRoleMenus(role.getRoleId(), role.getMenuIds());
        return ok;
    }

    /** 批量保存角色菜单关联 */
    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                sysRoleMenuMapper.insert(rm);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRoleByIds(List<Long> ids) {
        // 删除角色菜单关联
        for (Long roleId : ids) {
            sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getRoleId, roleId));
        }
        return removeByIds(ids);
    }

    @Override
    public List<Long> getMenuIds(Long roleId) {
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenus(Long roleId, List<Long> menuIds) {
        // 先删旧关联
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        // 再插新关联
        saveRoleMenus(roleId, menuIds);
        return true;
    }
}
