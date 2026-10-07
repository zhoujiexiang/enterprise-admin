package com.enterprise.admin.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprise.admin.common.BusinessException;
import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.dto.EmployeeExportDTO;
import com.enterprise.admin.dto.EmployeeQueryDTO;
import com.enterprise.admin.entity.Employee;
import com.enterprise.admin.entity.SysDept;
import com.enterprise.admin.mapper.EmployeeMapper;
import com.enterprise.admin.mapper.SysDeptMapper;
import com.enterprise.admin.security.UserContext;
import com.enterprise.admin.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工服务实现
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    public PageResult<Employee> page(EmployeeQueryDTO query) {
        Page<Employee> p = new Page<>(query.getPage(), query.getSize());
        IPage<Employee> result = employeeMapper.selectEmployeePage(p, query);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public Employee getEmployeeById(Long empId) {
        Employee emp = getById(empId);
        if (emp != null) {
            // 查询部门名
            SysDept dept = sysDeptMapper.selectById(emp.getDeptId());
            if (dept != null) {
                emp.setDeptName(dept.getDeptName());
            }
        }
        return emp;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        employee.setCreateBy(UserContext.getUsername());
        employee.setCreateTime(LocalDateTime.now());
        return save(employee);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        employee.setUpdateBy(UserContext.getUsername());
        employee.setUpdateTime(LocalDateTime.now());
        return updateById(employee);
    }

    @Override
    public boolean removeEmployeeByIds(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public boolean changeStatus(Long empId, String status) {
        Employee emp = new Employee();
        emp.setEmpId(empId);
        emp.setStatus(status);
        emp.setUpdateBy(UserContext.getUsername());
        emp.setUpdateTime(LocalDateTime.now());
        return updateById(emp);
    }

    @Override
    public void export(EmployeeQueryDTO query, HttpServletResponse response) {
        // 查询全部匹配数据（不分页，size 设大值）
        Page<Employee> p = new Page<>(1, 100000);
        p.setSearchCount(false);
        IPage<Employee> pageResult = employeeMapper.selectEmployeePage(p, query);
        List<Employee> employees = pageResult.getRecords();
        // 转换为导出 DTO（性别、状态做中文转换）
        List<EmployeeExportDTO> exportList = employees.stream().map(e -> {
            EmployeeExportDTO dto = new EmployeeExportDTO();
            dto.setName(e.getName());
            dto.setGender("0".equals(e.getGender()) ? "男" : "女");
            dto.setPhone(e.getPhone());
            dto.setDeptName(e.getDeptName());
            dto.setPosition(e.getPosition());
            dto.setSalary(e.getSalary());
            dto.setHireDate(e.getHireDate());
            dto.setStatus("0".equals(e.getStatus()) ? "在职" : "离职");
            return dto;
        }).collect(Collectors.toList());
        // 写 Excel 到响应输出流
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=employee.xlsx");
            EasyExcel.write(response.getOutputStream(), EmployeeExportDTO.class)
                    .sheet("员工信息")
                    .doWrite(exportList);
        } catch (Exception e) {
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }
}
