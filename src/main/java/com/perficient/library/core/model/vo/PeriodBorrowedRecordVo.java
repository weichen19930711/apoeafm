package com.perficient.library.core.model.vo;

import java.io.Serializable;

public class PeriodBorrowedRecordVo implements Serializable {

    private static final long serialVersionUID = 3955805289595954076L;

    private Integer recordAmount;

    private String date;

    public Integer getRecordAmount() {
        return recordAmount;
    }

    public void setRecordAmount(Integer recordAmount) {
        this.recordAmount = recordAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
