package com.perficient.library.core.model.vo;

import java.io.Serializable;

public class EmployeeOverdueRecordVo implements Serializable {

    private static final long serialVersionUID = -8077800562280678518L;

    private Integer overdueAmount;

    private Integer employeeId;

    private String employeeName;

    public Integer getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(Integer overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

}
