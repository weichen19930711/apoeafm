package com.perficient.library.core.model.vo;

import java.io.Serializable;

public class BorrowedPropertyVo implements Serializable {

    private static final long serialVersionUID = 1136116289188694356L;

    private Integer borrowedAmount;

    private Integer propertyId;

    private String propertyName;

    public Integer getBorrowedAmount() {
        return borrowedAmount;
    }

    public void setBorrowedAmount(Integer borrowedAmount) {
        this.borrowedAmount = borrowedAmount;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

}
