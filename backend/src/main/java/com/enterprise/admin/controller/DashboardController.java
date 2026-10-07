package com.enterprise.admin.controller;

import com.enterprise.admin.common.Result;
import com.enterprise.admin.dto.DashboardVO;
import com.enterprise.admin.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据大屏接口
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /** 概览统计 */
    @GetMapping("/overview")
    public Result<DashboardVO.Overview> overview() {
        return Result.success(dashboardService.overview());
    }

    /** 近 6 个月入职趋势 */
    @GetMapping("/trend")
    public Result<List<DashboardVO.TrendItem>> trend() {
        return Result.success(dashboardService.trend());
    }

    /** 各部门人数占比 */
    @GetMapping("/deptRatio")
    public Result<List<DashboardVO.DeptRatioItem>> deptRatio() {
        return Result.success(dashboardService.deptRatio());
    }

    /** 各部门平均薪资排名 */
    @GetMapping("/salaryRank")
    public Result<List<DashboardVO.SalaryRankItem>> salaryRank() {
        return Result.success(dashboardService.salaryRank());
    }
}
