package com.enterprise.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单与权限
 * 注意：sys_menu 表没有 deleted 字段，不继承 BaseEntity
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    /** 菜单ID */
    @TableId(type = IdType.AUTO)
    private Long menuId;

    /** 父菜单ID(0=顶级) */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 路由地址 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 权限标识 */
    private String perms;

    /** 菜单类型(M=目录 C=菜单 F=按钮) */
    private String menuType;

    /** 图标 */
    private String icon;

    /** 显示顺序 */
    private Integer orderNum;

    /** 是否显示(0=显示 1=隐藏) */
    private String visible;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 子菜单（不映射数据库） */
    @TableField(exist = false)
    private List<SysMenu> children;
}
