package com.enterprise.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 数据大屏 VO
 */
@Data
public class DashboardVO {

    /** 概览统计 */
    @Data
    public static class Overview {
        /** 员工总数 */
        private Long totalEmp;
        /** 在职人数 */
        private Long activeEmp;
        /** 部门数量 */
        private Long deptCount;
        /** 本月新增 */
        private Long monthNew;
    }

    /** 入职趋势项 */
    @Data
    public static class TrendItem {
        /** 月份 */
        private String month;
        /** 入职人数 */
        private Long count;
    }

    /** 部门人数占比项 */
    @Data
    public static class DeptRatioItem {
        /** 部门名称 */
        private String deptName;
        /** 人数 */
        private Long count;
    }

    /** 部门薪资排名项 */
    @Data
    public static class SalaryRankItem {
        /** 部门名称 */
        private String deptName;
        /** 平均薪资 */
        private BigDecimal avgSalary;
    }
}
