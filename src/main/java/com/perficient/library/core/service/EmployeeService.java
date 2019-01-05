package com.perficient.library.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.perficient.library.core.enums.Role;
import com.perficient.library.core.model.Employee;

public interface EmployeeService extends BaseService<Employee, Integer> {

    List<Employee> findAll(Pageable pageable);

    List<Employee> findByScreenNameContaining(String query);

    Employee findByEmid(String tptEmployeeId);

    Page<Employee> findByRole(Role role, Pageable pageable);

    Employee findByScreenName(String screenName);

    List<Employee> findEmployeeBorrowedRecordRank(Date startDate, Date endDate, Integer size);

    Integer findEmployeeBorrowedRecordsByEmployeeId(Integer employeeId);

    List<Employee> findEmployeeOverdueRecordRank(Date startDate, Date endDate, Integer size);

    Integer findEmployeeOverdueRecordsByEmployeeId(Integer employeeId);

}
