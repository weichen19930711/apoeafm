package com.perficient.library.core.model.vo;

import java.io.Serializable;

public class EmployeeBorrowedRecordVo implements Serializable {

    private static final long serialVersionUID = -8077800562280678518L;

    private Integer borrowedAmount;

    private Integer employeeId;

    private String employeeName;

    public Integer getBorrowedAmount() {
        return borrowedAmount;
    }

    public void setBorrowedAmount(Integer borrowedAmount) {
        this.borrowedAmount = borrowedAmount;
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
