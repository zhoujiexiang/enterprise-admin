package com.enterprise.admin.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工导出 Excel 行对象
 */
@Data
public class EmployeeExportDTO {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("部门")
    private String deptName;

    @ExcelProperty("职位")
    private String position;

    @ExcelProperty("薪资")
    private BigDecimal salary;

    @ExcelProperty("入职日期")
    private LocalDate hireDate;

    @ExcelProperty("状态")
    private String status;
}
