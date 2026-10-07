package com.enterprise.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.admin.dto.DashboardVO;
import com.enterprise.admin.entity.Employee;
import com.enterprise.admin.entity.SysDept;
import com.enterprise.admin.mapper.EmployeeMapper;
import com.enterprise.admin.mapper.SysDeptMapper;
import com.enterprise.admin.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据大屏服务实现
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    public DashboardVO.Overview overview() {
        DashboardVO.Overview o = new DashboardVO.Overview();
        // 员工总数（deleted=0 由 @TableLogic 自动过滤）
        o.setTotalEmp(employeeMapper.selectCount(null));
        // 在职人数
        o.setActiveEmp(employeeMapper.selectCount(
                new LambdaQueryWrapper<Employee>().eq(Employee::getStatus, "0")));
        // 部门数量
        o.setDeptCount(sysDeptMapper.selectCount(null));
        // 本月新增（hire_date 在当月）
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        o.setMonthNew(employeeMapper.selectCount(
                new LambdaQueryWrapper<Employee>().ge(Employee::getHireDate, monthStart)));
        return o;
    }

    @Override
    public List<DashboardVO.TrendItem> trend() {
        LocalDate now = LocalDate.now();
        LocalDate sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1);
        // 查询近 6 个月入职的员工
        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>().ge(Employee::getHireDate, sixMonthsAgo));
        // 按年月分组统计
        Map<String, Long> monthCount = new HashMap<>();
        for (Employee e : employees) {
            if (e.getHireDate() != null) {
                String month = e.getHireDate().getYear() + "-"
                        + String.format("%02d", e.getHireDate().getMonthValue());
                monthCount.merge(month, 1L, Long::sum);
            }
        }
        // 构建近 6 个月的趋势列表
        List<DashboardVO.TrendItem> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String monthStr = month.getYear() + "-"
                    + String.format("%02d", month.getMonthValue());
            DashboardVO.TrendItem item = new DashboardVO.TrendItem();
            item.setMonth(monthStr);
            item.setCount(monthCount.getOrDefault(monthStr, 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<DashboardVO.DeptRatioItem> deptRatio() {
        List<SysDept> depts = sysDeptMapper.selectList(null);
        // 查询所有在职员工
        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>().eq(Employee::getStatus, "0"));
        // 按部门分组计数
        Map<Long, Long> countByDept = new HashMap<>();
        for (Employee e : employees) {
            countByDept.merge(e.getDeptId(), 1L, Long::sum);
        }
        List<DashboardVO.DeptRatioItem> result = new ArrayList<>();
        for (SysDept d : depts) {
            DashboardVO.DeptRatioItem item = new DashboardVO.DeptRatioItem();
            item.setDeptName(d.getDeptName());
            item.setCount(countByDept.getOrDefault(d.getDeptId(), 0L));
            result.add(item);
        }
        // 按人数降序
        result.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return result;
    }

    @Override
    public List<DashboardVO.SalaryRankItem> salaryRank() {
        List<SysDept> depts = sysDeptMapper.selectList(null);
        // 查询所有在职员工
        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>().eq(Employee::getStatus, "0"));
        // 按部门分组累计薪资和人数
        Map<Long, BigDecimal> sumByDept = new HashMap<>();
        Map<Long, Long> countByDept = new HashMap<>();
        for (Employee e : employees) {
            if (e.getSalary() != null) {
                sumByDept.merge(e.getDeptId(), e.getSalary(), BigDecimal::add);
                countByDept.merge(e.getDeptId(), 1L, Long::sum);
            }
        }
        List<DashboardVO.SalaryRankItem> result = new ArrayList<>();
        for (SysDept d : depts) {
            DashboardVO.SalaryRankItem item = new DashboardVO.SalaryRankItem();
            item.setDeptName(d.getDeptName());
            Long count = countByDept.get(d.getDeptId());
            if (count != null && count > 0) {
                item.setAvgSalary(sumByDept.get(d.getDeptId())
                        .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
            } else {
                item.setAvgSalary(BigDecimal.ZERO);
            }
            result.add(item);
        }
        // 按平均薪资降序
        result.sort((a, b) -> b.getAvgSalary().compareTo(a.getAvgSalary()));
        return result;
    }
}
