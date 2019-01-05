package com.perficient.library.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.perficient.library.core.model.BorrowRecord;
import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.OverdueRecord;
import com.perficient.library.core.model.vo.OverdueRecordVo;

public interface OverdueRecordService extends BaseService<OverdueRecord, Integer> {

    OverdueRecord findByBorrowRecord(BorrowRecord borrowRecord);

    Page<OverdueRecord> findByEmployee(Employee employee, Pageable pageable);

    Page<OverdueRecord> findAllOverdueRecords(Pageable pageable);
    
    Page<OverdueRecord> findNotCheckedInOverdueRecords(Pageable pageable);

    List<OverdueRecord> findNotCheckedInOverdueRecordsByEmployee(Employee employee);

    Page<OverdueRecord> findNotCheckedInOverdueRecordsByEmployee(Employee employee, Pageable pageable);

    Page<OverdueRecord> findOverdueRecordsBySearchValue(Specification<OverdueRecord> spec, Pageable pageable);

    void exportOverdueRecord(List<OverdueRecordVo> records);
}
