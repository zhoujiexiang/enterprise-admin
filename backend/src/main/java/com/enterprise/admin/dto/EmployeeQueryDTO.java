package com.enterprise.admin.dto;

import lombok.Data;

/**
 * 员工分页查询参数
 */
@Data
public class EmployeeQueryDTO {

    /** 页码，默认 1 */
    private int page = 1;

    /** 每页大小，默认 10 */
    private int size = 10;

    /** 员工姓名（模糊） */
    private String name;

    /** 部门ID */
    private Long deptId;

    /** 状态(0=在职 1=离职) */
    private String status;

    /** 入职起始日期(yyyy-MM-dd) */
    private String startDate;

    /** 入职结束日期(yyyy-MM-dd) */
    private String endDate;
}
