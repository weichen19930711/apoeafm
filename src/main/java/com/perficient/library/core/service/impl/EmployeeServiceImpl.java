package com.perficient.library.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.perficient.library.core.enums.Role;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.repository.EmployeeRepository;
import com.perficient.library.core.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee save(Employee entity) {
        return employeeRepository.save(entity);
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findOne(Integer id) {
        return employeeRepository.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        employeeRepository.delete(id);
    }

    @Override
    public boolean exists(Integer id) {
        return employeeRepository.exists(id);
    }

    @Override
    public List<Employee> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable).getContent();
    }

    @Override
    public List<Employee> findByScreenNameContaining(String query) {
        return employeeRepository.findByScreenNameContaining(query);
    }

    @Override
    public Employee findByEmid(String tptEmployeeId) {
        return employeeRepository.findByEmid(tptEmployeeId);
    }

    @Override
    public Page<Employee> findByRole(Role role, Pageable pageable) {
        return employeeRepository.findByRole(role, pageable);
    }

    @Override
    public Employee findByScreenName(String screenName) {
        return employeeRepository.findByScreenName(screenName);
    }

    @Override
    public List<Employee> findEmployeeBorrowedRecordRank(Date startDate, Date endDate, Integer size) {
        return employeeRepository.findEmployeeBorrowedRecordRank(startDate, endDate, size);
    }

    @Override
    public Integer findEmployeeBorrowedRecordsByEmployeeId(Integer employeeId) {
        return employeeRepository.findEmployeeBorrowedRecordsByEmployeeId(employeeId);
    }

    @Override
    public List<Employee> findEmployeeOverdueRecordRank(Date startDate, Date endDate, Integer size) {
        return employeeRepository.findEmployeeOverdueRecordRank(startDate, endDate, size);
    }

    @Override
    public Integer findEmployeeOverdueRecordsByEmployeeId(Integer employeeId) {
        return employeeRepository.findEmployeeOverdueRecordsByEmployeeId(employeeId);
    }

}
