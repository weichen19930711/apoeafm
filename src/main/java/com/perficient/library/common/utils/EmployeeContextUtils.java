package com.perficient.library.common.utils;

import com.perficient.library.core.model.Employee;

public class EmployeeContextUtils {

    private static final String EMP_KEY = "emp";

    private EmployeeContextUtils() {
    }

    public static void addEmpToSession(Employee employee) {
        ServletUtils.setSessionAttribute(EMP_KEY, employee);
    }

    public static Employee getEmpInSession() {
        Object emp = ServletUtils.getSessionAttribute(EMP_KEY);
        return emp instanceof Employee ? (Employee) emp : null;
    }

    public static void removeEmpInSession() {
        ServletUtils.removeSessionAttribute(EMP_KEY);
    }

}
