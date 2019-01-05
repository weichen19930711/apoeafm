package com.perficient.library.core.model.vo;

import java.io.Serializable;

public class CategoryBorrowedPropertyVo implements Serializable {

    private static final long serialVersionUID = -990059733196701024L;

    private Integer borrowedAmount;

    private Integer categoryId;

    private String categoryName;

    public Integer getBorrowedAmount() {
        return borrowedAmount;
    }

    public void setBorrowedAmount(Integer borrowedAmount) {
        this.borrowedAmount = borrowedAmount;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
