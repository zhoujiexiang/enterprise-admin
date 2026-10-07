package com.enterprise.admin.service;

import com.enterprise.admin.dto.DashboardVO;

import java.util.List;

/**
 * 数据大屏服务
 */
public interface DashboardService {

    /** 概览统计 */
    DashboardVO.Overview overview();

    /** 近 6 个月入职趋势 */
    List<DashboardVO.TrendItem> trend();

    /** 各部门人数占比 */
    List<DashboardVO.DeptRatioItem> deptRatio();

    /** 各部门平均薪资排名 */
    List<DashboardVO.SalaryRankItem> salaryRank();
}
