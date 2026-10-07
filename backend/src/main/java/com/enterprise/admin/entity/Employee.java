package com.enterprise.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employee")
public class Employee extends BaseEntity {

    /** 员工ID */
    @TableId(type = IdType.AUTO)
    private Long empId;

    /** 姓名 */
    private String name;

    /** 性别(0=男 1=女) */
    private String gender;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门ID */
    private Long deptId;

    /** 职位 */
    private String position;

    /** 薪资 */
    private BigDecimal salary;

    /** 入职日期 */
    private LocalDate hireDate;

    /** 状态(0=在职 1=离职) */
    private String status;

    /** 部门名称（联表查询返回，不映射数据库） */
    @TableField(exist = false)
    private String deptName;
}
