package com.enterprise.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 前端路由树节点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterVO {

    /** 路由名称 */
    private String name;

    /** 路由地址 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 是否隐藏 */
    private Boolean hidden;

    /** 子路由 */
    private List<RouterVO> children;
}
