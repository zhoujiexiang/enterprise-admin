package com.enterprise.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色ID */
    @TableId(type = IdType.AUTO)
    private Long roleId;

    /** 角色名称 */
    private String roleName;

    /** 角色权限标识 */
    private String roleKey;

    /** 状态(0=正常 1=停用) */
    private String status;

    /** 备注 */
    private String remark;

    /** 菜单 ID 列表（不映射数据库） */
    @TableField(exist = false)
    private List<Long> menuIds;

    /** 用户 ID 列表（不映射数据库） */
    @TableField(exist = false)
    private List<Long> userIds;
}
