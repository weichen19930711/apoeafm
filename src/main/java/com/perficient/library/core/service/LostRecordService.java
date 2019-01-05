package com.perficient.library.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.perficient.library.core.model.Employee;
import com.perficient.library.core.model.LostRecord;

public interface LostRecordService extends BaseService<LostRecord, Integer> {

    List<LostRecord> findNotPaidLostRecordsByEmployee(Employee employee);

    Page<LostRecord> getLostRecords(Specification<LostRecord> spec, Pageable pageable);

    void exportLostRecords(Object object);
}
