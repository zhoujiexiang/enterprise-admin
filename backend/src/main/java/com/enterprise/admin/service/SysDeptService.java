package com.enterprise.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprise.admin.entity.SysDept;

import java.util.List;

/**
 * 部门服务
 */
public interface SysDeptService extends IService<SysDept> {

    /** 查询所有部门构建树形 */
    List<SysDept> tree();

    /** 新增部门 */
    boolean addDept(SysDept dept);

    /** 修改部门 */
    boolean updateDept(SysDept dept);

    /** 删除部门（检查是否有子部门） */
    boolean removeDept(Long deptId);
}
