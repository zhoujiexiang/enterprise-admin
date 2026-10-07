package com.enterprise.admin.controller;

import com.enterprise.admin.common.Result;
import com.enterprise.admin.entity.SysDept;
import com.enterprise.admin.security.PreAuthorize;
import com.enterprise.admin.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理接口
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    /** 查询部门树 */
    @GetMapping("/tree")
    @PreAuthorize("system:dept:list")
    public Result<List<SysDept>> tree() {
        return Result.success(sysDeptService.tree());
    }

    /** 查询部门详情 */
    @GetMapping("/{id}")
    @PreAuthorize("system:dept:list")
    public Result<SysDept> getById(@PathVariable Long id) {
        return Result.success(sysDeptService.getById(id));
    }

    /** 新增部门 */
    @PostMapping
    @PreAuthorize("system:dept:add")
    public Result<Void> add(@RequestBody SysDept dept) {
        sysDeptService.addDept(dept);
        return Result.success();
    }

    /** 修改部门 */
    @PutMapping
    @PreAuthorize("system:dept:edit")
    public Result<Void> update(@RequestBody SysDept dept) {
        sysDeptService.updateDept(dept);
        return Result.success();
    }

    /** 删除部门（检查是否有子部门） */
    @DeleteMapping("/{id}")
    @PreAuthorize("system:dept:remove")
    public Result<Void> remove(@PathVariable Long id) {
        sysDeptService.removeDept(id);
        return Result.success();
    }
}
