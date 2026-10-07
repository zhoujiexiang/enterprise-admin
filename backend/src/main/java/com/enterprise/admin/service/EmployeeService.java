package com.enterprise.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprise.admin.common.PageResult;
import com.enterprise.admin.dto.EmployeeQueryDTO;
import com.enterprise.admin.entity.Employee;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 员工服务
 */
public interface EmployeeService extends IService<Employee> {

    /** 分页查询员工（带部门名） */
    PageResult<Employee> page(EmployeeQueryDTO query);

    /** 查询员工详情 */
    Employee getEmployeeById(Long empId);

    /** 新增员工 */
    boolean addEmployee(Employee employee);

    /** 修改员工 */
    boolean updateEmployee(Employee employee);

    /** 批量逻辑删除员工 */
    boolean removeEmployeeByIds(List<Long> ids);

    /** 修改员工状态 */
    boolean changeStatus(Long empId, String status);

    /** 导出 Excel */
    void export(EmployeeQueryDTO query, HttpServletResponse response);
}
