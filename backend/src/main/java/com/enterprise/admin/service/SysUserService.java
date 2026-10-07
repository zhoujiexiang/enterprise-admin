package com.enterprise.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.entity.SysUser;

import java.util.List;

/**
 * 用户服务
 */
public interface SysUserService extends IService<SysUser> {

    /** 分页查询用户 */
    PageResult<SysUser> page(int page, int size, String username, String phone, String status);

    /** 查询用户详情（含角色ID列表） */
    SysUser getUserById(Long userId);

    /** 新增用户（密码 BCrypt 加密 + 保存角色关联） */
    boolean addUser(SysUser user);

    /** 修改用户（更新用户 + 重建角色关联） */
    boolean updateUser(SysUser user);

    /** 批量逻辑删除用户 */
    boolean removeUserByIds(List<Long> ids);

    /** 重置密码（需校验原密码） */
    boolean resetPwd(Long userId, String oldPassword, String newPassword);

    /** 修改状态 */
    boolean changeStatus(Long userId, String status);
}
