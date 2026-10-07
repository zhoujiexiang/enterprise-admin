package com.enterprise.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprise.admin.common.BusinessException;
import com.enterprise.admin.dto.RouterVO;
import com.enterprise.admin.entity.SysMenu;
import com.enterprise.admin.mapper.SysMenuMapper;
import com.enterprise.admin.security.UserContext;
import com.enterprise.admin.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> tree() {
        return tree(null);
    }

    @Override
    public List<SysMenu> tree(String keyword) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        // 只查目录(M)和菜单(C)，不含按钮(F)
        wrapper.in(SysMenu::getMenuType, "M", "C");
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysMenu::getMenuName, keyword);
        }
        wrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> allMenus = list(wrapper);
        return buildTree(allMenus);
    }

    /** 递归构建菜单树，parentId=0 为根节点 */
    private List<SysMenu> buildTree(List<SysMenu> allMenus) {
        List<SysMenu> roots = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (menu.getParentId() != null && menu.getParentId() == 0L) {
                roots.add(menu);
            }
        }
        for (SysMenu root : roots) {
            root.setChildren(getChildren(root.getMenuId(), allMenus));
        }
        return roots;
    }

    private List<SysMenu> getChildren(Long parentId, List<SysMenu> allMenus) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (parentId.equals(menu.getParentId())) {
                children.add(menu);
            }
        }
        for (SysMenu child : children) {
            child.setChildren(getChildren(child.getMenuId(), allMenus));
        }
        return children;
    }

    @Override
    public boolean addMenu(SysMenu menu) {
        menu.setCreateBy(UserContext.getUsername());
        menu.setCreateTime(LocalDateTime.now());
        return save(menu);
    }

    @Override
    public boolean updateMenu(SysMenu menu) {
        menu.setUpdateBy(UserContext.getUsername());
        menu.setUpdateTime(LocalDateTime.now());
        return updateById(menu);
    }

    @Override
    public boolean removeMenu(Long menuId) {
        // 检查是否有子菜单
        long count = count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
        if (count > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        return removeById(menuId);
    }

    @Override
    public Set<String> getUserPerms(Long userId) {
        if (userId != null && userId == 1L) {
            // admin 返回所有按钮权限
            List<SysMenu> allMenus = list(new LambdaQueryWrapper<SysMenu>()
                    .ne(SysMenu::getPerms, "")
                    .eq(SysMenu::getMenuType, "F"));
            return allMenus.stream().map(SysMenu::getPerms).collect(Collectors.toSet());
        }
        return baseMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<RouterVO> buildMenus(Long userId) {
        // 查询用户拥有的菜单（目录 M + 菜单 C）
        List<SysMenu> menus = baseMapper.selectMenusByUserId(userId);
        // 构建树形
        List<SysMenu> tree = buildTree(menus);
        // 转换为 RouterVO 树
        return buildRouters(tree);
    }

    /** 将菜单树转换为前端路由树 */
    private List<RouterVO> buildRouters(List<SysMenu> menus) {
        List<RouterVO> routers = new ArrayList<>();
        for (SysMenu menu : menus) {
            RouterVO router = new RouterVO();
            router.setName(menu.getMenuName());
            router.setPath(menu.getPath());
            router.setComponent(menu.getComponent());
            router.setHidden("1".equals(menu.getVisible()));
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                router.setChildren(buildRouters(menu.getChildren()));
            }
            routers.add(router);
        }
        return routers;
    }
}
