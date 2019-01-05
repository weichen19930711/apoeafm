package com.perficient.library.web.controller.restful;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.annotation.PermissionRequired;
import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.common.utils.PageUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.RestServiceException;
import com.perficient.library.core.exception.UnauthorizedException;
import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.service.BorrowRecordService;
import com.perficient.library.core.service.EmployeeService;
import com.perficient.library.core.service.OverdueRecordService;
import com.perficient.library.web.domain.Pagination;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Only Librarian can get all employees. Only Librarian can get/add/delete librarians.
 * 
 * @author bin.zhou
 *
 */
@RestController
@RequestMapping("/api/v1/employee")
@Api("employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private OverdueRecordService overdueRecordService;

    @GetMapping
    @ApiOperation("(Librarian Only) get all employees")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<Employee>> getAllEmployees() {
        return ReturnResultUtils.success(employeeService.findAll());
    }

    @GetMapping("/librarian")
    @ApiOperation("(Librarian Only) get all librarians")
    public ReturnResult<List<Employee>> getAllLibrarians(@RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {
        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));
        Page<Employee> librarianResult = employeeService.findByRole(Role.LIBRARIAN, pageable);
        List<Employee> librarianList = librarianResult.getContent();

        return ReturnResultUtils.successPaged(librarianList, librarianResult.getTotalElements());
    }

    @GetMapping("/search")
    @ApiOperation("(Librarian Only) serach employees by screen name")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<List<Employee>> searchEmplyeeByScreenName(@RequestParam("q") String q) {
        return ReturnResultUtils.success(employeeService.findByScreenNameContaining(q));
    }

    @PostMapping("/librarian")
    @ApiOperation("(Librarian Only) add a librarian")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Employee> addLibrarian(@RequestParam("employeeName") String employeeName) {

        Employee dbEmployee = null;
        if ((dbEmployee = employeeService.findByScreenName(employeeName)) == null) {
            throw new RestServiceException("the employee is not exist");
        }

        if (dbEmployee.getRole().getPoints() == Role.LIBRARIAN.getPoints()) {
            throw new RestServiceException("the employee already have librarian authority");
        }

        if (dbEmployee.getRole().getPoints() > Role.LIBRARIAN.getPoints()) {
            throw new RestServiceException("the employee already have higher authority");
        }

        dbEmployee.setRole(Role.LIBRARIAN);
        dbEmployee = employeeService.save(dbEmployee);
        return ReturnResultUtils.success("add librarian succeeded", dbEmployee);
    }

    @DeleteMapping("/librarian")
    @ApiOperation("(Librarian Only) delete a librarian")
    @PermissionRequired(role = Role.LIBRARIAN)
    public ReturnResult<Employee> deleteLibrarian(@RequestParam("employeeName") String employeeName) {

        Employee dbEmployee = null;
        if ((dbEmployee = employeeService.findByScreenName(employeeName)) == null) {
            throw new RestServiceException("the employee is not exist");
        }

        // cannot delete himself
        Employee operator = EmployeeContextUtils.getEmpInSession();
        if (employeeName.equalsIgnoreCase(operator.getScreenName())) {
            throw new RestServiceException("you cannot remove yourself from librarian group");
        }

        if (dbEmployee.getRole().getPoints() > operator.getRole().getPoints()) {
            throw new RestServiceException(
                "you cannot remove a employee with authority higher than you from librarian group");
        }

        dbEmployee.setRole(Role.EMPLOYEE);
        dbEmployee = employeeService.save(dbEmployee);
        return ReturnResultUtils.success("delete librarian succeeded", dbEmployee);
    }

    @GetMapping("/borrow_record")
    @ApiOperation("get employee's borrow records")
    public ReturnResult<List<BorrowRecord>> getBorrowRecords(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        Employee employee = EmployeeContextUtils.getEmpInSession();
        if (employee == null) {
            throw new UnauthorizedException();
        }
        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));
        Page<BorrowRecord> records = borrowRecordService.findByEmployee(employee, pageable);
        return ReturnResultUtils.successPaged(records.getContent(), records.getTotalElements());
    }

    @GetMapping("/borrow_record_no_return")
    @ApiOperation("get employee's not checked in borrow records")
    public ReturnResult<List<BorrowRecord>> getNotCheckedInBorrowRecords() {

        Employee employee = EmployeeContextUtils.getEmpInSession();
        if (employee == null) {
            throw new UnauthorizedException();
        }
        return ReturnResultUtils.success(borrowRecordService.findNotCheckedInBorrowRecordsByEmployee(employee));
    }

    @GetMapping("/overdue_record")
    @ApiOperation("get employee's not checked in overdue records")
    public ReturnResult<List<OverdueRecord>> findNotCheckedInOverdueRecords(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        Employee employee = EmployeeContextUtils.getEmpInSession();
        if (employee == null) {
            throw new UnauthorizedException();
        }
        Pageable pageable = PageUtils.buildPageRequest(Pagination.generatePagnation(page, size));
        Page<OverdueRecord> records = overdueRecordService.findNotCheckedInOverdueRecordsByEmployee(employee, pageable);
        return ReturnResultUtils.successPaged(records.getContent(), records.getTotalElements());
    }

    @PutMapping("/login_status")
    @ApiOperation("employee agree agreement frist login")
    public void employeeAgreeAgreement() {

        Employee currentEmployee = EmployeeContextUtils.getEmpInSession();
        if (currentEmployee == null) {
            throw new UnauthorizedException();
        }
        currentEmployee.setIsFirstLogin(false);
        employeeService.save(currentEmployee);
    }
}
