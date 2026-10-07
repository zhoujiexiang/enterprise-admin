package com.enterprise.admin.controller;

import com.enterprise.admin.common.Result;
import com.enterprise.admin.entity.SysMenu;
import com.enterprise.admin.security.PreAuthorize;
import com.enterprise.admin.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理接口
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    /** 查询菜单树（可选关键词搜索） */
    @GetMapping("/tree")
    @PreAuthorize("system:menu:list")
    public Result<List<SysMenu>> tree(@RequestParam(required = false) String query) {
        return Result.success(sysMenuService.tree(query));
    }

    /** 查询菜单详情 */
    @GetMapping("/{id}")
    @PreAuthorize("system:menu:list")
    public Result<SysMenu> getById(@PathVariable Long id) {
        return Result.success(sysMenuService.getById(id));
    }

    /** 新增菜单 */
    @PostMapping
    @PreAuthorize("system:menu:add")
    public Result<Void> add(@RequestBody SysMenu menu) {
        sysMenuService.addMenu(menu);
        return Result.success();
    }

    /** 修改菜单 */
    @PutMapping
    @PreAuthorize("system:menu:edit")
    public Result<Void> update(@RequestBody SysMenu menu) {
        sysMenuService.updateMenu(menu);
        return Result.success();
    }

    /** 删除菜单（检查是否有子菜单） */
    @DeleteMapping("/{id}")
    @PreAuthorize("system:menu:remove")
    public Result<Void> remove(@PathVariable Long id) {
        sysMenuService.removeMenu(id);
        return Result.success();
    }
}
