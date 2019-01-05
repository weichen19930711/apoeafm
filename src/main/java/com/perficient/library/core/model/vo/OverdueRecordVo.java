package com.perficient.library.core.model.vo;

import java.util.Date;

import com.perficient.library.common.utils.DateFormatUtil;
import com.perficient.library.core.model.OverdueRecord;

public class OverdueRecordVo extends OverdueRecord {

    private static final long serialVersionUID = -5945399220695578085L;

    private Integer overdueDays;

    private OverdueRecord overdueRecord;

    public OverdueRecordVo() {

    }

    public OverdueRecordVo(OverdueRecord overdueRecord) {
        this.setId(overdueRecord.getId());
        this.setBorrowRecord(overdueRecord.getBorrowRecord());
        this.setCreateDate(overdueRecord.getCreateDate());
        this.setReturned(overdueRecord.isReturned());
        this.overdueDays = DateFormatUtil.daysBetween(this.getBorrowRecord().getDueDate(), new Date());
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public OverdueRecord getOverdueRecord() {
        return overdueRecord;
    }

    public void setOverdueRecord(OverdueRecord overdueRecord) {
        this.overdueRecord = overdueRecord;
    }

}
