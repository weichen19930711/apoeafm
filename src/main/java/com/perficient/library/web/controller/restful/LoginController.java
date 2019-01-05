package com.perficient.library.web.controller.restful;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.library.common.utils.EmployeeContextUtils;
import com.perficient.library.common.utils.ReturnResultUtils;
import com.perficient.library.core.enums.Role;
import com.perficient.library.core.exception.ValidationFailedException;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.service.EmployeeService;
import com.perficient.library.core.service.LoginService;
import com.perficient.library.web.domain.ReturnResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1")
@Api("login_and_logout")
public class LoginController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private LoginService loginService;
    
    @GetMapping("/login")
    @ApiOperation("login")
    @SuppressWarnings("unchecked")
    public ReturnResult<Employee> login(@RequestParam(value = "ticket") String ticket,
        @RequestParam(value = "service", required = false) String service) {
        
        Map<String, Object> validateSuccessResult = loginService.validateTicket(ticket, service);
        if (validateSuccessResult == null) {
            throw new ValidationFailedException("validate failed");
        }
        
        String screenName = (String) validateSuccessResult.get("user");
        
        Map<String, Object> attributesMap = (Map<String, Object>) validateSuccessResult.get("attributes");
        List<String> employeeIds = (List<String>) attributesMap.get("EmployeeID");
        String emid = null;
        if (employeeIds != null && employeeIds.isEmpty()) {
            emid = employeeIds.get(0);
        }
        
        Employee emp = employeeService.findByScreenName(screenName);
        if (emp == null) {
            emp = new Employee();
            emp.setScreenName(screenName);
            emp.setEmid(emid);
            // there is a system account in CAS that screen name called librarian 
            if ("librarian".equals(screenName)) {
                emp.setRole(Role.LIBRARIAN);
            } else {
                emp.setRole(Role.EMPLOYEE);
            }
            // default isFirstLogin is true
            emp.setIsFirstLogin(true);
            emp = employeeService.save(emp);
        }
        EmployeeContextUtils.addEmpToSession(emp);
        return ReturnResultUtils.success("login succeeded", emp);
        
    }

    @GetMapping("/loginByScreenName")
    @ApiOperation("loginByScreenName")
    @SuppressWarnings("unchecked")
    public ReturnResult<Employee> loginByScreenName(@RequestParam(value = "ticket") String screenName) {
        Employee emp = employeeService.findByScreenName(screenName);
        if(emp != null) {
            EmployeeContextUtils.addEmpToSession(emp);
        } else {
            return ReturnResultUtils.error(screenName + "is no exist");
        }
        return ReturnResultUtils.success("login succeeded", emp);

    }
    
    @GetMapping("/logout")
    @ApiOperation("logout")
    public ReturnResult<String> logout(HttpServletRequest req) {
        EmployeeContextUtils.removeEmpInSession();
        return ReturnResultUtils.success("logout succeeded");
    }
    
}
