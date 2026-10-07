package com.enterprise.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprise.admin.dto.RouterVO;
import com.enterprise.admin.entity.SysMenu;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务
 */
public interface SysMenuService extends IService<SysMenu> {

    /** 查询所有菜单构建树形 */
    List<SysMenu> tree();

    /** 关键词搜索菜单构建树形 */
    List<SysMenu> tree(String keyword);

    /** 新增菜单 */
    boolean addMenu(SysMenu menu);

    /** 修改菜单 */
    boolean updateMenu(SysMenu menu);

    /** 删除菜单（检查是否有子菜单） */
    boolean removeMenu(Long menuId);

    /** 获取用户拥有的权限标识集合 */
    Set<String> getUserPerms(Long userId);

    /** 构建前端路由树 */
    List<RouterVO> buildMenus(Long userId);
}
