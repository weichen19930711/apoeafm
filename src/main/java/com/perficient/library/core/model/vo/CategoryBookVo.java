package com.perficient.library.core.model.vo;

import java.io.Serializable;

public class CategoryBookVo implements Serializable {

    private static final long serialVersionUID = -636080800346378643L;

    private Integer bookAmount;

    private Integer categoryId;

    private String categoryName;

    public Integer getBookAmount() {
        return bookAmount;
    }

    public void setBookAmount(Integer bookAmount) {
        this.bookAmount = bookAmount;
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
