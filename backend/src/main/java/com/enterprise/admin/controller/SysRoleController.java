package com.enterprise.admin.controller;

import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.common.Result;
import com.enterprise.admin.entity.SysRole;
import com.enterprise.admin.security.PreAuthorize;
import com.enterprise.admin.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理接口
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    /** 分页查询角色 */
    @GetMapping("/page")
    @PreAuthorize("system:role:list")
    public Result<PageResult<SysRole>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleKey,
            @RequestParam(required = false) String status) {
        return Result.success(sysRoleService.page(page, size, roleName, roleKey, status));
    }

    /** 查询角色详情（含菜单ID列表） */
    @GetMapping("/{id}")
    @PreAuthorize("system:role:list")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getRoleById(id));
    }

    /** 新增角色 */
    @PostMapping
    @PreAuthorize("system:role:add")
    public Result<Void> add(@RequestBody SysRole role) {
        sysRoleService.addRole(role);
        return Result.success();
    }

    /** 修改角色 */
    @PutMapping
    @PreAuthorize("system:role:edit")
    public Result<Void> update(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return Result.success();
    }

    /** 批量删除角色（ids 用逗号分隔） */
    @DeleteMapping("/{ids}")
    @PreAuthorize("system:role:remove")
    public Result<Void> removeByIds(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        sysRoleService.removeRoleByIds(idList);
        return Result.success();
    }

    /** 查询角色关联的菜单ID列表 */
    @GetMapping("/{id}/menuIds")
    @PreAuthorize("system:role:list")
    public Result<List<Long>> getMenuIds(@PathVariable Long id) {
        return Result.success(sysRoleService.getMenuIds(id));
    }

    /** 给角色分配菜单 */
    @PutMapping("/{id}/menus")
    @PreAuthorize("system:role:edit")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(id, menuIds);
        return Result.success();
    }
}
