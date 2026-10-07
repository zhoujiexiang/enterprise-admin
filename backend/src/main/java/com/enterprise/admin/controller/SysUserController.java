package com.enterprise.admin.controller;

import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.common.Result;
import com.enterprise.admin.dto.ResetPwdDTO;
import com.enterprise.admin.entity.SysUser;
import com.enterprise.admin.security.PreAuthorize;
import com.enterprise.admin.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /** 分页查询用户 */
    @GetMapping("/page")
    @PreAuthorize("system:user:list")
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status) {
        return Result.success(sysUserService.page(page, size, username, phone, status));
    }

    /** 查询用户详情（含角色ID列表） */
    @GetMapping("/{id}")
    @PreAuthorize("system:user:list")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getUserById(id));
    }

    /** 新增用户 */
    @PostMapping
    @PreAuthorize("system:user:add")
    public Result<Void> add(@RequestBody @Valid SysUser user) {
        sysUserService.addUser(user);
        return Result.success();
    }

    /** 修改用户 */
    @PutMapping
    @PreAuthorize("system:user:edit")
    public Result<Void> update(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return Result.success();
    }

    /** 批量删除用户（ids 用逗号分隔） */
    @DeleteMapping("/{ids}")
    @PreAuthorize("system:user:remove")
    public Result<Void> removeByIds(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        sysUserService.removeUserByIds(idList);
        return Result.success();
    }

    /** 重置密码 */
    @PutMapping("/resetPwd")
    @PreAuthorize("system:user:edit")
    public Result<Void> resetPwd(@RequestBody @Valid ResetPwdDTO dto) {
        sysUserService.resetPwd(dto.getUserId(), dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    /** 修改状态 */
    @PutMapping("/changeStatus")
    @PreAuthorize("system:user:edit")
    public Result<Void> changeStatus(@RequestBody SysUser user) {
        sysUserService.changeStatus(user.getUserId(), user.getStatus());
        return Result.success();
    }
}
