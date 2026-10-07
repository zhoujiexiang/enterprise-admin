package com.enterprise.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.entity.SysRole;

import java.util.List;

/**
 * 角色服务
 */
public interface SysRoleService extends IService<SysRole> {

    /** 分页查询角色 */
    PageResult<SysRole> page(int page, int size, String roleName, String roleKey, String status);

    /** 查询角色详情（含菜单ID列表） */
    SysRole getRoleById(Long roleId);

    /** 新增角色 */
    boolean addRole(SysRole role);

    /** 修改角色 */
    boolean updateRole(SysRole role);

    /** 批量逻辑删除角色（同时删除 role_menu 关联） */
    boolean removeRoleByIds(List<Long> ids);

    /** 查询角色关联的菜单ID列表 */
    List<Long> getMenuIds(Long roleId);

    /** 给角色分配菜单（先删旧关联再插新） */
    boolean assignMenus(Long roleId, List<Long> menuIds);
}
