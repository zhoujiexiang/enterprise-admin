package com.enterprise.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enterprise.admin.dto.EmployeeQueryDTO;
import com.enterprise.admin.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 员工 Mapper
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 分页查询员工（带部门名）
     */
    IPage<Employee> selectEmployeePage(IPage<Employee> page, @Param("query") EmployeeQueryDTO query);
}
