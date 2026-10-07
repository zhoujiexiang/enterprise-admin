package com.enterprise.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprise.admin.common.BusinessException;
import com.enterprise.admin.entity.SysDept;
import com.enterprise.admin.mapper.SysDeptMapper;
import com.enterprise.admin.security.UserContext;
import com.enterprise.admin.service.SysDeptService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门服务实现
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<SysDept> tree() {
        List<SysDept> allDepts = list(new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getParentId)
                .orderByAsc(SysDept::getOrderNum));
        return buildTree(allDepts);
    }

    /** 递归构建部门树，parentId=0 为根节点 */
    private List<SysDept> buildTree(List<SysDept> allDepts) {
        List<SysDept> roots = new ArrayList<>();
        for (SysDept dept : allDepts) {
            if (dept.getParentId() != null && dept.getParentId() == 0L) {
                roots.add(dept);
            }
        }
        for (SysDept root : roots) {
            root.setChildren(getChildren(root.getDeptId(), allDepts));
        }
        return roots;
    }

    private List<SysDept> getChildren(Long parentId, List<SysDept> allDepts) {
        List<SysDept> children = new ArrayList<>();
        for (SysDept dept : allDepts) {
            if (parentId.equals(dept.getParentId())) {
                children.add(dept);
            }
        }
        for (SysDept child : children) {
            child.setChildren(getChildren(child.getDeptId(), allDepts));
        }
        return children;
    }

    @Override
    public boolean addDept(SysDept dept) {
        dept.setCreateBy(UserContext.getUsername());
        dept.setCreateTime(LocalDateTime.now());
        return save(dept);
    }

    @Override
    public boolean updateDept(SysDept dept) {
        dept.setUpdateBy(UserContext.getUsername());
        dept.setUpdateTime(LocalDateTime.now());
        return updateById(dept);
    }

    @Override
    public boolean removeDept(Long deptId) {
        // 检查是否有子部门
        long count = count(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, deptId));
        if (count > 0) {
            throw new BusinessException("存在子部门，不允许删除");
        }
        return removeById(deptId);
    }
}
