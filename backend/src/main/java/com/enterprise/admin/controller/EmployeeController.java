package com.enterprise.admin.controller;

import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.common.Result;
import com.enterprise.admin.dto.EmployeeQueryDTO;
import com.enterprise.admin.entity.Employee;
import com.enterprise.admin.security.PreAuthorize;
import com.enterprise.admin.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工管理接口
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /** 分页查询员工 */
    @GetMapping("/page")
    @PreAuthorize("employee:list")
    public Result<PageResult<Employee>> page(EmployeeQueryDTO query) {
        return Result.success(employeeService.page(query));
    }

    /** 查询员工详情 */
    @GetMapping("/{id}")
    @PreAuthorize("employee:list")
    public Result<Employee> getById(@PathVariable Long id) {
        return Result.success(employeeService.getEmployeeById(id));
    }

    /** 新增员工 */
    @PostMapping
    @PreAuthorize("employee:add")
    public Result<Void> add(@RequestBody Employee employee) {
        employeeService.addEmployee(employee);
        return Result.success();
    }

    /** 修改员工 */
    @PutMapping
    @PreAuthorize("employee:edit")
    public Result<Void> update(@RequestBody Employee employee) {
        employeeService.updateEmployee(employee);
        return Result.success();
    }

    /** 批量删除员工（ids 用逗号分隔） */
    @DeleteMapping("/{ids}")
    @PreAuthorize("employee:remove")
    public Result<Void> removeByIds(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        employeeService.removeEmployeeByIds(idList);
        return Result.success();
    }

    /** 修改员工状态 */
    @PutMapping("/changeStatus")
    @PreAuthorize("employee:edit")
    public Result<Void> changeStatus(@RequestBody Employee employee) {
        employeeService.changeStatus(employee.getEmpId(), employee.getStatus());
        return Result.success();
    }

    /** 导出员工 Excel */
    @GetMapping("/export")
    @PreAuthorize("employee:export")
    public void export(EmployeeQueryDTO query, HttpServletResponse response) {
        employeeService.export(query, response);
    }
}
